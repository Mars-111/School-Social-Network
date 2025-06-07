package ru.kors.timelineservice.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.kors.timelineservice.models.TimelineModel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@AllArgsConstructor
@Slf4j
public class TimelineService {
    private final JdbcTemplate jdbcTemplate;
    private final ConcurrentMap<Long, TimelineModel> chatMap = new ConcurrentHashMap<>();
    private final BlockingQueue<Long> saveQueue = new LinkedBlockingQueue<>();
    private final Set<Long> enqueuedChats = ConcurrentHashMap.newKeySet();
    private final ExecutorService saverExecutor = Executors.newSingleThreadExecutor(createThreadFactory("timeline-saver", true));
    private final ScheduledExecutorService scheduledSaver = Executors.newSingleThreadScheduledExecutor(createThreadFactory("timeline-scheduler", true));
    private static final int CHANGES_BEFORE_SAVE = 3;

    // Структура блокировок для синхронизации по chatId без String.intern
    private final ReentrantLock[] stripedLocks = new ReentrantLock[64]; // В будущем увеличить

    {
        for (int i = 0; i < stripedLocks.length; i++) {
            stripedLocks[i] = new ReentrantLock();
        }
    }

    private ReentrantLock getLock(Long chatId) {
        return stripedLocks[Math.abs(chatId.hashCode() % stripedLocks.length)];
    }

    @PostConstruct
    public void startSaver() {
        initDb();

        saverExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Long chatId = saveQueue.take();
                    enqueuedChats.remove(chatId);
                    TimelineModel timeline = chatMap.get(chatId);
                    if (timeline != null && timeline.getUnsavedChanges() > 0) {
                        saveToDb(chatId, timeline.getTimelineId());
                        timeline.resetUnsavedChanges();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Saver thread interrupted, terminating...");
                } catch (Exception e) {
                    log.error("Ошибка в потоке сохранения: {}", e.getMessage(), e);
                }
            }
        });

        scheduledSaver.scheduleAtFixedRate(() -> {
            log.info("scheduledSaver - Периодическое сохранение всех изменённых timeline'ов...");
            for (Map.Entry<Long, TimelineModel> entry : chatMap.entrySet()) {
                Long chatId = entry.getKey();
                TimelineModel timeline = entry.getValue();
                if (timeline.getUnsavedChanges() > 0 && enqueuedChats.add(chatId)) {
                    saveQueue.offer(chatId);
                }
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    public int getNextTimelineId(long chatId) {
        TimelineModel timeline = chatMap.get(chatId);
        if (timeline == null) {
            ReentrantLock lock = getLock(chatId);
            lock.lock();
            try {
                timeline = chatMap.computeIfAbsent(chatId, this::loadFromDb);
            } finally {
                lock.unlock();
            }
        }

        int nextId = timeline.incrementAndGet();
        if (timeline.getUnsavedChanges() >= CHANGES_BEFORE_SAVE && enqueuedChats.add(chatId)) {
            saveQueue.offer(chatId);
        }

        log.info("getNextTimelineId - Следующий ID для чата {}: {}", chatId, nextId);
        return nextId;
    }

    private TimelineModel loadFromDb(Long chatId) {
        try {
            log.info("loadFromDb - Загрузка timeline из БД для чата {}", chatId);
            Integer id = jdbcTemplate.queryForObject(
                    "SELECT timeline_id FROM chat_timeline WHERE chat_id = ?",
                    Integer.class,
                    chatId
            );
            return new TimelineModel(id != null ? id : 0);
        } catch (EmptyResultDataAccessException e) {
            log.info("loadFromDb - Чат {} не найден в БД, создаём новый timeline", chatId);
            return new TimelineModel(0);
        } catch (Exception e) {
            log.error("loadFromDb - Ошибка при загрузке чата {}: {}", chatId, e.getMessage(), e);
            throw e;
        }
    }

    private void saveToDb(long chatId, int timelineId) {
        log.info("saveToDb - Сохраняем timeline: chatId={}, timelineId={}", chatId, timelineId);
        jdbcTemplate.update(
                "INSERT INTO chat_timeline (chat_id, timeline_id) VALUES (?, ?) " +
                        "ON CONFLICT (chat_id) DO UPDATE SET timeline_id = EXCLUDED.timeline_id",
                chatId, timelineId
        );
    }

    private void initDb() {
        log.info("initDb - Инициализация таблицы timeline");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS chat_timeline (
                    chat_id BIGINT PRIMARY KEY,
                    timeline_id INTEGER NOT NULL
                )
                """);
    }

    @PreDestroy
    public void shutdown() {
        log.info("shutdown - Завершаем сервис TimelineService...");
        try {
            saverExecutor.shutdownNow();
            scheduledSaver.shutdownNow();
            flushAll();
        } catch (Exception e) {
            log.error("shutdown - Ошибка при остановке сервиса: {}", e.getMessage(), e);
        } finally {
            saveQueue.clear();
            enqueuedChats.clear();
        }
        log.info("shutdown - Завершено.");
    }

    private void flushAll() {
        log.info("flushAll - Принудительное сохранение всех изменённых timeline'ов...");
        for (Map.Entry<Long, TimelineModel> entry : chatMap.entrySet()) {
            TimelineModel timeline = entry.getValue();
            if (timeline.getUnsavedChanges() > 0) {
                saveToDb(entry.getKey(), timeline.getTimelineId());
                timeline.resetUnsavedChanges();
            }
        }
    }

    private static ThreadFactory createThreadFactory(String name, boolean daemon) {
        return r -> {
            Thread t = new Thread(r);
            t.setName(name);
            t.setDaemon(daemon);
            return t;
        };
    }
}
