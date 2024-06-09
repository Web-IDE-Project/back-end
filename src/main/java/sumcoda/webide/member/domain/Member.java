package sumcoda.webide.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.member.enumerate.Role;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;

import java.util.ArrayList;
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
    private String username;

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


    // 사용자의 권한을 나타내기위한 role
    // ex) ADMIN, USER
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 하나의 유저는 여러가지 컨테이너 생성가능 1:N 양방향
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<MemberWorkspace> memberWorkspaces = new ArrayList<>();

    // 빌더 패턴 생성자
    @Builder
    public Member(String username, String password, String nickname, String email, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    // 직접 빌더 패턴의 생성자를 활용하지 말고 해당 메서드를 활용하여 엔티티 생성
    public static Member createMember(String username, String password, String nickname, String email, Role role) {
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .role(role)
                .build();
    }

    // Member 1 <-> N MemberWorkspace
    // 양방향 연관관계 편의 메서드
    public void addMemberWorkspace(MemberWorkspace memberWorkspace) {
        this.memberWorkspaces.add(memberWorkspace);

        if (memberWorkspace.getMember() != this) {
            memberWorkspace.assignMember(this);
        }
    }

}
