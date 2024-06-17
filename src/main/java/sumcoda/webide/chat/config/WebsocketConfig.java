package sumcoda.webide.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * STOMP 프로토콜을 사용하는 WebSocket 엔드포인트를 등록
   * @param registry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // /api/ws 경로를 통해 클라이언트가 WebSocket에 연결
    registry.addEndpoint("/api/ws")
            .setAllowedOriginPatterns("*");
  }

  /**
   * 메시지 브로커 구성
   * @param registry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 클라이언트가 메시지를 보낼 때 사용할 경로
    registry.setApplicationDestinationPrefixes("/api/pub");
    // 클라이언트에게 메시지를 전달할 때 사용할 경로
    registry.enableSimpleBroker("/api/sub");
  }

}
