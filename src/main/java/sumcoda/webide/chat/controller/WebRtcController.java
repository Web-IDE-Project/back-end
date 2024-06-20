package sumcoda.webide.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import sumcoda.webide.chat.dto.WebRtcSignalDTO;

import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebRtcController {

  private final SimpMessagingTemplate template;
  private final Map<Long, List<String>> workspacePeers = new HashMap<>();

  @MessageMapping("/webrtc/{workspaceId}/join")
  public void handleJoin(@Payload WebRtcSignalDTO signal, @DestinationVariable Long workspaceId, SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    workspacePeers.computeIfAbsent(workspaceId, k -> new ArrayList<>());
    if (!workspacePeers.get(workspaceId).contains(sessionId)) {
      workspacePeers.get(workspaceId).add(sessionId);
    }
    log.info("Peer joined: {} in workspace: {}", sessionId, workspaceId);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/peers", workspacePeers.get(workspaceId));
  }

  @MessageMapping("/webrtc/{workspaceId}/leave")
  public void handleLeave(@Payload WebRtcSignalDTO signal, @DestinationVariable Long workspaceId, SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    List<String> peers = workspacePeers.get(workspaceId);
    if (peers != null) {
      peers.remove(sessionId);
      log.info("Peer left: {} in workspace: {}", sessionId, workspaceId);
      template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/peers", peers);
    }
  }

  @MessageMapping("/webrtc/{workspaceId}/offer")
  public void handleOffer(@Payload WebRtcSignalDTO signal, @DestinationVariable Long workspaceId) {
    log.info("Offer from: {} in workspace: {}", signal.getPeerId(), workspaceId);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/offer", signal);
  }

  @MessageMapping("/webrtc/{workspaceId}/answer")
  public void handleAnswer(@Payload WebRtcSignalDTO signal, @DestinationVariable Long workspaceId) {
    log.info("Answer from: {} in workspace: {}", signal.getPeerId(), workspaceId);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/answer", signal);
  }

  @MessageMapping("/webrtc/{workspaceId}/ice-candidate")
  public void handleIceCandidate(@Payload WebRtcSignalDTO signal, @DestinationVariable Long workspaceId) {
    log.info("ICE candidate from: {} in workspace: {}", signal.getPeerId(), workspaceId);
    template.convertAndSend("/api/sub/webrtc/" + workspaceId + "/ice-candidate", signal);
  }
}
