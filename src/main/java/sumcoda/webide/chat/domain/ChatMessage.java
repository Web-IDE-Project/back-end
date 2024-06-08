package sumcoda.webide.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.chat.enumerate.MessageType;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.workspace.domain.Workspace;

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
    public ChatMessage(String message, MessageType messageType, ChatRoom chatRoom, Member member) {
        this.message = message;
        this.messageType = messageType;
        this.assignChatRoom(chatRoom);
        this.assignMember(member);
    }

    // 직접 빌더 패턴의 생성자를 활용하지 말고 해당 메서드를 활용하여 엔티티 생성
    public static ChatMessage createChatMessage(String message, MessageType messageType, ChatRoom chatRoom, Member member) {
        return ChatMessage.builder()
                .message(message)
                .messageType(messageType)
                .chatRoom(chatRoom)
                .member(member)
                .build();
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

    // ChatMessage N -> 1 Member
    // 단방향 메서드
    public void assignMember(Member member) {
        this.member = member;
    }
}