package sumcoda.webide.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sumcoda.webide.chat.enumerate.MessageType;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequestDTO {

  private MessageType messageType;
  private String message;

  @Builder
  public ChatMessageRequestDTO(String message, MessageType messageType){
    this.message = message;
    this.messageType = messageType;
  }
}
