package sumcoda.webide.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.workspace.domain.Workspace;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 연관관게 주인
    // 양방향 연관관계
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    // 양방향 연관관계
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    private List<ChatMessage> chatMessages;

    @Builder
    public ChatRoom(String name, Workspace workspace) {
        this.name = name;
        this.assignWorkspace(workspace);
    }

    // ChatRoom 1 <-> 1 Workspace
    // 양방향 연관관계 편의 메서드드
    public void assignWorkspace(Workspace workspace) {
        if (this.workspace != null) {
            this.workspace.assignChatRoom(null);
        }
        this.workspace = workspace;
        if (workspace != null && workspace.getChatRoom() != this) {
            workspace.assignChatRoom(this);
        }
    }

    // ChatRoom 1 <-> N ChatMessage
    // 양방향 연관관계 편의 메서드
    public void addChatMessage(ChatMessage chatMessage) {
        this.chatMessages.add(chatMessage);

        if (chatMessage.getChatRoom() != this) {
            chatMessage.assignChatRoom(this);
        }
    }
}