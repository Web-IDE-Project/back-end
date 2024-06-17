package sumcoda.webide.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.chat.domain.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  Optional<ChatRoom> findByWorkspaceId(Long workspaceId);
}
