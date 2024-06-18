package sumcoda.webide.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebRtcController {

  private final SimpMessagingTemplate template;

  @MessageMapping("/webrtc/{workspaceId}/offer")
  public void handleOffer(@DestinationVariable Long workspaceId, String offer) {
    log.info("Received offer in workspace {}: {}", workspaceId, offer);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/offer", offer);
  }

  @MessageMapping("/webrtc/{workspaceId}/answer")
  public void handleAnswer(@DestinationVariable Long workspaceId, String answer) {
    log.info("Received answer in workspace {}: {}", workspaceId, answer);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/answer", answer);
  }

  @MessageMapping("/webrtc/{workspaceId}/ice-candidate")
  public void handleIceCandidate(@DestinationVariable Long workspaceId, String candidate) {
    log.info("Received ICE candidate in workspace {}: {}", workspaceId, candidate);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/ice-candidate", candidate);
  }

}
