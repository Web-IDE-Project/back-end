package sumcoda.webide.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.chat.enumerate.MessageType;
import sumcoda.webide.member.domain.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private MessageType messageType;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ChatMessage(String message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    // ChatMessage N <-> 1 MembChatRoomer
    // 양방향 연관관계 편의 메서드드
    public void assignChatRoom(ChatRoom chatRoom) {
        if (this.chatRoom != null) {
            this.chatRoom.getChatMessages().remove(this);
        }
        this.chatRoom = chatRoom;

        if (!chatRoom.getChatMessages().contains(this)) {
            chatRoom.addChatMessage(this);
        }
    }
}