package sumcoda.webide.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.chat.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
