package sumcoda.webide.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.chat.domain.ChatMessage;
import sumcoda.webide.chat.domain.ChatRoom;
import sumcoda.webide.chat.dto.request.ChatMessageRequestDTO;
import sumcoda.webide.chat.dto.response.ChatMessageResponseDTO;
import sumcoda.webide.chat.enumerate.MessageType;
import sumcoda.webide.chat.exception.BaseException;
import sumcoda.webide.chat.repository.ChatMessageRepository;
import sumcoda.webide.chat.repository.ChatRoomRepository;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;

  @Transactional
  public ChatMessageResponseDTO saveMessage(ChatMessageRequestDTO requestDto, String username, Long workspaceId) {
    Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BaseException("User not found"));

    ChatRoom chatRoom = chatRoomRepository.findByWorkspaceId(workspaceId)
            .orElseThrow(() -> new BaseException("Chat room not found"));

    String nickName = member.getNickname();

    ChatMessage message;

    // messageType 에 따라 message 내용 구성
    if(MessageType.ENTER.equals(requestDto.getMessageType())){
      message = ChatMessage.createChatMessage("[입장] " + nickName + "님이 입장했습니다.", requestDto.getMessageType(),
              chatRoom, member);
    } else if (MessageType.EXIT.equals(requestDto.getMessageType())){
      message = ChatMessage.createChatMessage("[퇴장] " + nickName + "님이 퇴장하셨습니다.", requestDto.getMessageType(),
              chatRoom, member);
    } else {
      message = ChatMessage.createChatMessage(requestDto.getMessage(), requestDto.getMessageType(),
              chatRoom, member);
    }

    ChatMessage savedMessage = chatMessageRepository.save(message);

    return ChatMessageResponseDTO.from(savedMessage);
  }
}
