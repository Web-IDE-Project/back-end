package sumcoda.webide.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.chat.domain.ChatMessage;
import sumcoda.webide.chat.domain.ChatRoom;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
}
