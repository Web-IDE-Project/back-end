package sumcoda.webide.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.chat.domain.ChatMessage;
import sumcoda.webide.chat.enumerate.MessageType;

@Getter
@NoArgsConstructor
public class ChatMessageResponseDTO {
  private String message;
  private MessageType messageType;
  private String senderName;

  @Builder
  public ChatMessageResponseDTO(String message, MessageType messageType, String senderName) {
    this.message = message;
    this.messageType = messageType;
    this.senderName = senderName;
  }

  public static ChatMessageResponseDTO from(ChatMessage chatMessage) {
    return ChatMessageResponseDTO.builder()
            .message(chatMessage.getMessage())
            .messageType(chatMessage.getMessageType())
            .senderName(chatMessage.getMember().getNickname())
            .build();
  }
}
