package sumcoda.webide.workspace.terminal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import sumcoda.webide.member.auth.social.CustomOAuth2User;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.service.WorkspaceService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TerminalWebsocketController {

  private final TerminalWebsocketService terminalWebsocketService;

  private final WorkspaceService workspaceService;

  private final SimpMessagingTemplate template;

  /**
   * 주어진 명령어를 실행하고 결과를 반환
   * @param workspaceId 워크스페이스 ID
   * @param request 실행할 명령어
   * @param headerAccessor 메시지 헤더 접근자
   */
  @MessageMapping("/terminal/{workspaceId}")
  public void executeTerminal(
          @DestinationVariable Long workspaceId,
          @Payload CommandRequestDTO request,
          SimpMessageHeaderAccessor headerAccessor,
          Authentication authentication
  ) throws IOException, InterruptedException {
    String username;

    if (authentication instanceof OAuth2AuthenticationToken) {
      // OAuth2.0 사용자
      CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
      username = oauthUser.getUsername();
      log.info("유저이름 : " + username);


      // 그외 사용자
    } else {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      username = userDetails.getUsername();
      log.info("유저이름 : " + username);
    }

    // WebSocket 의 세션 ID를 사용하여 각 사용자의 현재 경로를 관리
    String sessionId = headerAccessor.getSessionId();

    String result = terminalWebsocketService.executeCommand(workspaceId, request.getCommand(), sessionId);
    WorkspaceEntriesResponseDTO allEntriesByWorkspaceId = workspaceService.getAllEntriesByWorkspaceId(workspaceId, username);

    Map<String, Object> responseData = new HashMap<>();
    responseData.put("result", result);
    responseData.put("allEntries", allEntriesByWorkspaceId);

    template.convertAndSend("/api/sub/terminal/" + workspaceId, responseData);

  }
}
