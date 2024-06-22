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
  private String senderId;
  private String senderName;
  private String senderProfileImageUrl;

  @Builder
  public ChatMessageResponseDTO(String message, MessageType messageType, String senderId, String senderName, String senderProfileImageUrl) {
    this.message = message;
    this.messageType = messageType;
    this.senderId = senderId;
    this.senderName = senderName;
    this.senderProfileImageUrl = senderProfileImageUrl;
  }

  public static ChatMessageResponseDTO from(ChatMessage chatMessage) {
    return ChatMessageResponseDTO.builder()
            .message(chatMessage.getMessage())
            .messageType(chatMessage.getMessageType())
            .senderId(chatMessage.getMember().getUsername())
            .senderName(chatMessage.getMember().getNickname())
            .senderProfileImageUrl(chatMessage.getMember().getProfileImage() != null ?
                    chatMessage.getMember().getProfileImage().getAwsS3SavedFileURL() :
                    null)
            .build();
  }
}
