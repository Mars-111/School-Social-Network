package ru.kors.timelineservice.models;

import java.util.concurrent.atomic.AtomicInteger;


public class TimelineModel {
    private final AtomicInteger timelineId;
    private final AtomicInteger unsavedChanges = new AtomicInteger(0);

    public TimelineModel(int initial) {
        this.timelineId = new AtomicInteger(initial);
    }

    public int incrementAndGet() {
        unsavedChanges.incrementAndGet();
        return timelineId.incrementAndGet();
    }

    public int getTimelineId() {
        return timelineId.get();
    }

    public int getUnsavedChanges() {
        return unsavedChanges.get();
    }

    public void resetUnsavedChanges() {
        unsavedChanges.set(0);
    }
}
