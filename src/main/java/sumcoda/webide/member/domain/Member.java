package sumcoda.webide.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.member.enumerate.Role;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일반 로그인 = 아이디
    // 소셜 로그인 = provider + " " + providerId
    // ex) naver asdf1234as93hf8932hf9ah2393hf9ah39fha9shd9fht
    @Column(nullable = false)
    private String userId;

    // 비밀번호
    // 일반 로그인 = 회원가입시 직접 설정해야함
    // 소셜 로그인 = 설정하지 않아도됨
    @Column(nullable = false)
    private String password;

    // 애플리케이션에서 활동할 이름
    // 일반 로그인 = 직접 설정 가능
    // 소셜 로그인 = 해당 계정에 이미 등록된 이름으로 설정됨
    @Column(nullable = false)
    private String nickname;

    // 이메일
    // 일반 로그임 = 회원가입시 설정 가능
    // 소셜 로그인 = 해당 계정에 이미 등록된 이메일로 설정됨
    @Column(nullable = false)
    private String email;


    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    // 하나의 유저는 여러가지 컨테이너 생성가능 1:N 양방향
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<MemberWorkspace> memberWorkspaces;

    // Member 1 <-> N MemberWorkspace
    // 양방향 연관관계 편의 메서드
    public void addMemberWorkspace(MemberWorkspace memberWorkspace) {
        this.memberWorkspaces.add(memberWorkspace);

        if (memberWorkspace.getMember() != this) {
            memberWorkspace.assignMember(this);
        }
    }

}
