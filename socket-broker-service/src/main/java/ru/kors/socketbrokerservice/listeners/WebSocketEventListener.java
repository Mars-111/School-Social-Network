//package ru.kors.socketbrokerservice.listeners;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class WebSocketEventListener {
//
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
//    private final Map<String, Boolean> reconnectingUsers = new ConcurrentHashMap<>();
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sub = (String) headerAccessor.getSessionAttributes().get("sub");
//        if (sub != null) {
//            if (reconnectingUsers.containsKey(sub)) {
//                log.info("User reconnected: {}", sub);
//                reconnectingUsers.remove(sub);
//                return;
//            }
////            log.info("User join: {}", sub);
////            var chatMessage = MessagePayload.builder()
////                    .type("JOIN")
////                    .content(".")
////                    .sender(sub)
////                    .build();
//            //messagingTemplate.convertAndSend("/topic/all", chatMessage);
//        }
//        else {
//            log.warn("User not authenticated");
//        }
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sub = (String) headerAccessor.getSessionAttributes().get("sub");
//
//        if (sub != null) {
//            reconnectingUsers.put(sub, true);
//            scheduler.schedule(() -> {
//                if (reconnectingUsers.containsKey(sub)) {
//                    log.info("User did not reconnect within 2 minutes: {}", sub);
//                    log.info("User disconnected: {}", sub);
////                    var chatMessage = MessagePayload.builder()
////                            .type("LEAVE")
////                            .content(".")
////                            .sender(sub)
////                            .build();
////                    messagingTemplate.convertAndSend("/topic/all", chatMessage);
//                    reconnectingUsers.remove(sub);
//                }
//            }, 4, TimeUnit.SECONDS); //4 секунды на реконект //В БУДУЩЕМ ЗАМЕНИТЬ НА РЕАКТ В КЛИЕНТЕ
//        }
//    }
//
//}
