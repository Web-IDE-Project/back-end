package sumcoda.webide.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.chat.domain.ChatRoom;

@Getter
@NoArgsConstructor
public class ChatRoomResponseDTO {
  private Long id;
  private String name;
  private Long workspaceId;

  @Builder
  public ChatRoomResponseDTO(Long id, String name, Long workspaceId){
    this.id = id;
    this.name = name;
    this.workspaceId = workspaceId;
  }

  public static ChatRoomResponseDTO from(ChatRoom chatRoom) {
    return ChatRoomResponseDTO.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .workspaceId(chatRoom.getWorkspace().getId())
            .build();
  }
}

