package sumcoda.webide.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.workspace.domain.Workspace;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    private Workspace workspace;

    @Builder
    public ChatRoom(String name) {
        this.name = name;
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
}