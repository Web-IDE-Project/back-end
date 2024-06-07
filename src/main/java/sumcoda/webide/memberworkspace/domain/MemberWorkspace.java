package sumcoda.webide.memberworkspace.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.workspace.domain.Workspace;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 방에 참서한 유저의 역할
    // admin, editor, viewer
    private String role;

    // 해당 유저가 방에 참석한 시점
    private LocalDateTime joinedAt;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    // MemberWorkspace N <-> 1 Member
    // 양방향 연관관계 편의 메서드드
    public void assignMember(Member member) {
        if (this.member != null) {
            this.member.getMemberWorkspaces().remove(this);
        }
        this.member = member;

        if (!member.getMemberWorkspaces().contains(this)) {
            member.addMemberWorkspace(this);
        }
    }

    // MemberWorkspace N <-> 1 Workspace
    // 양방향 연관관계 편의 메서드드
    public void assignWorkspace(Workspace workspace) {
        if (this.workspace != null) {
            this.workspace.getMemberWorkspaces().remove(this);
        }
        this.workspace = workspace;

        if (!workspace.getMemberWorkspaces().contains(this)) {
            workspace.addMemberWorkspace(this);
        }
    }
}
