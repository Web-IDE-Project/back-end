package sumcoda.webide.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import sumcoda.webide.chat.config.WebSocketEventListener;
import sumcoda.webide.chat.dto.request.ChatMessageRequestDTO;
import sumcoda.webide.chat.dto.response.ChatMessageResponseDTO;
import sumcoda.webide.chat.service.ChatMessageService;
import sumcoda.webide.member.auth.social.CustomOAuth2User;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

  private final SimpMessagingTemplate template;
  private final ChatMessageService chatMessageService;

  private final WebSocketEventListener webSocketEventListener;

  /**
   * 메시지 송신 경로 => /api/pub/chat/{workspaceId}
   * @param workspaceId => workspaceId로 topic 구별
   * @param request
   */
  @MessageMapping("/chat/{workspaceId}")
  public void createChatMessage(
          @DestinationVariable Long workspaceId,
          @Payload ChatMessageRequestDTO request,
          Authentication authentication
  ) {

    /** 클라이언트에서 보내는 내용 ChatMessageRequestDto
     * {
     * 	messageType : ENTER, TALK, EXIT
     * 	message : 메시지 내용
     * }
     */

    String username ;
    if (authentication instanceof OAuth2AuthenticationToken) {
      // OAuth2.0 사용자
      CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
      username = oauthUser.getUsername();

      // 그외 사용자
    } else {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      username = userDetails.getUsername();
    }

    /** 클라이언트에서 받을 내용 ChatMessageResponseDto
     * {
     * 	messageType : ENTER, TALK, EXIT
     * 	message : 메시지 내용
     * 	senderName : 보내는 사람 닉네임
     * }
     */

    // 메시지 저장, 반환
    ChatMessageResponseDTO message = chatMessageService.saveMessage(request, username, workspaceId);

    /**
     * 메시지 수신 경로
     * /api/sub/chat/{workspaceId}
     */

    log.info(String.valueOf(message));
    template.convertAndSend("/api/sub/chat/" + workspaceId, message);

  }

  /**
   * 채팅방에 참여한 인원 수 실시간 송수신
   * connect, disconnect 시 마다 구독자 수를 요청하고 업데이트 하도록
   * @param workspaceId
   */
  @MessageMapping("/chat/{workspaceId}/count")
  public void sendSubscriberCount(@DestinationVariable Long workspaceId) {
    int subscriberCount = webSocketEventListener.getSubscriberCount("/api/sub/chat/" + workspaceId);
    template.convertAndSend("/api/sub/chat/" + workspaceId + "/count", subscriberCount);
  }

}
