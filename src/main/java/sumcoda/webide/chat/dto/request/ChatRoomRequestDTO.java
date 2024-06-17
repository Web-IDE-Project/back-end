package sumcoda.webide.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomRequestDTO {
  private Long workspaceId;

  @Builder
  public ChatRoomRequestDTO(Long workspaceId){
    this.workspaceId = workspaceId;
  }
}

