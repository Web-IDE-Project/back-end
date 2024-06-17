package sumcoda.webide.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.chat.domain.ChatRoom;
import sumcoda.webide.chat.dto.request.ChatRoomRequestDTO;
import sumcoda.webide.chat.dto.response.ChatRoomResponseDTO;
import sumcoda.webide.chat.exception.BaseException;
import sumcoda.webide.chat.repository.ChatRoomRepository;
import sumcoda.webide.workspace.domain.Workspace;
import sumcoda.webide.workspace.repository.WorkspaceRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

  private final WorkspaceRepository workspaceRepository;
  private final ChatRoomRepository chatRoomRepository;

  /**
   * 채팅방 생성 로직
   * @param chatRoomRequestDto
   * @return
   */
  // TODO : 워크스페이스 공유 시 공유된 워크스페이스 아이디로 채팅방을 생성하도록 해당 로직 사용
  @Transactional
  public ChatRoomResponseDTO createChatRoom(ChatRoomRequestDTO chatRoomRequestDto) {
    Workspace workspace = workspaceRepository.findById(chatRoomRequestDto.getWorkspaceId())
            .orElseThrow(() -> new BaseException("Workspace not found"));

    if (workspace.getChatRoom() != null) {
      throw new BaseException("Chat room already exists for this workspace");
    }

    // TODO : 웹소켓 topic을 workspaceId로 구분하고 있기 때문에 랜덤한 name이 현재는 필요 없는 상황. 다른 용도로 사용할 수 있을 지 고려 또는 엔티티에서 name 삭제 고려
    ChatRoom chatRoom = ChatRoom.createChatRoom(UUID.randomUUID().toString(), workspace);

    chatRoomRepository.save(chatRoom);

    return ChatRoomResponseDTO.from(chatRoom);
  }
}
