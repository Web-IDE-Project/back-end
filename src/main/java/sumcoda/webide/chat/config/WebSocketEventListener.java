package sumcoda.webide.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 웹 소켓 이벤트 발생 시마다 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final SimpMessagingTemplate template;

  // 채팅 방별 구독자 수 관리
  private final ConcurrentHashMap<String, AtomicInteger> roomSubscribers = new ConcurrentHashMap<>();

  // 세션 ID 별 구독 정보 관리
  private final ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> sessionSubscriptions = new ConcurrentHashMap<>();

  /**
   * subscribe listener
   * @param event
   */
  @EventListener
  public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    // 헤더로부터 sessionId, destination 얻어오기
    String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
    String destination = (String) event.getMessage().getHeaders().get("simpDestination");

    // 특정 채팅방 구독 시 구독자 수 증가
    if (sessionId != null && destination != null && destination.startsWith("/api/sub/chat/")) {
      roomSubscribers.computeIfAbsent(destination, k -> new AtomicInteger(0)).incrementAndGet();
      sessionSubscriptions.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>()).put(destination, true);

      // 채팅방 별 구독자 수 /count 경로로 전송
      int count = roomSubscribers.get(destination).get();
      log.info("User subscribed to " + destination + ". Subscriber count: " + count);
      template.convertAndSend(destination + "/count", count);
    }
  }

  /**
   * unsubscribe listener
   * @param event
   */
  @EventListener
  public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
    // 헤더로부터 sessionId, destination 얻어오기
    String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
    String destination = (String) event.getMessage().getHeaders().get("simpDestination");

    if (sessionId != null && destination != null && destination.startsWith("/api/sub/chat/")) {
      // sessionId의 구독 정보 가져오기
      ConcurrentHashMap<String, Boolean> sessionSubs = sessionSubscriptions.get(sessionId);
      if (sessionSubs != null && sessionSubs.remove(destination) != null) {
        AtomicInteger count = roomSubscribers.get(destination);
        // 구독자 수 감소, map에서 제거
        if (count != null && count.decrementAndGet() <= 0) {
          roomSubscribers.remove(destination);
        }
        int currentCount = (count != null) ? count.get() : 0;
        log.info("User unsubscribed from " + destination + ". Subscriber count: " + currentCount);
        template.convertAndSend(destination + "/count", currentCount);
      }
    }
  }

  /**
   * connect listener
   * @param event
   */
  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    log.info("User connected. Session ID: " + event.getMessage().getHeaders().get("simpSessionId"));
  }

  /**
   * disconnect listener
   * @param event
   */
  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    log.info("User disconnected. Session ID: " + sessionId);

    // 연결 해제 시 세션이 구독한 모든 채팅 방의 구독자 수 감소
    ConcurrentHashMap<String, Boolean> sessionSubs = sessionSubscriptions.remove(sessionId);
    if (sessionSubs != null) {
      sessionSubs.forEach((destination, value) -> {
        // 각 채팅방의 구독자 수 업데이트, /count 경로로 전송
        if (destination.startsWith("/api/sub/chat/")) {
          AtomicInteger count = roomSubscribers.get(destination);
          if (count != null && count.decrementAndGet() <= 0) {
            roomSubscribers.remove(destination);
          }
          int currentCount = (count != null) ? count.get() : 0;
          log.info("User disconnected from " + destination + ". Subscriber count: " + currentCount);
          template.convertAndSend(destination + "/count", currentCount);
        }
      });
    }
  }

  /**
   * 구독자 수 조회
   * @param topic
   * @return
   */
  public int getSubscriberCount(String topic) {
    AtomicInteger count = roomSubscribers.get(topic);
    return count != null ? count.get() : 0;
  }
}
