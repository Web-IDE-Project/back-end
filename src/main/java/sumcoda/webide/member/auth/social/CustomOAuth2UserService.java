package sumcoda.webide.member.auth.social;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.enumerate.Role;
import sumcoda.webide.member.repository.MemberRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 리소스 서버에서 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("oAuth2User = " + oAuth2User.getAttributes());

        // naver, google, kakao 등 값이 저장
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 응답 받은 데이터 저장하기 위한 변수 생성
        OAuth2UserInfo oAuth2UserInfo;

        // 네이버에서 제공된 데이터라면
        switch (registrationId) {
            case "naver":
                oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());

                // 구글에서 제공된 데이터라면
                break;
            case "google":
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

                break;
            case "kakao":
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

                break;
            default:
                return null;
        }

        String username = oAuth2UserInfo.getUsername();

        // 유저 이름을 바탕으로 DB 조회
        Member findMember = memberRepository.findByUsername(username).orElse(null);

        Role role = Role.USER;

        // 만약 신규 로그인 회원이라면
        if (findMember == null) {
            Member member = Member.builder()
                    .username(username)
                    .password(UUID.randomUUID().toString())
                    .nickname(oAuth2UserInfo.getName())
                    .email(oAuth2UserInfo.getEmail())
                    .role(Role.USER)
                    .build();

            memberRepository.save(member);

            // 신규 로그인 회원이 아니라면 현재 로그인한 유저 정보를 바탕으로 DB에 업데이트
        } else {

            // 해당 유저가 특정 경로에 접근할때 인가 작업에 필요한 role 값 업데이트
            role = findMember.getRole();
            // Dirty Checking 으로 업데이트 값 자동으로 DB 반영
            findMember.assignEmail(oAuth2UserInfo.getEmail());
        }

        // OAuth2LoginAuthenticationProvider에 OAuth2User 를 전달하고 로그인 완료
        return new CustomOAuth2User(oAuth2UserInfo, role);
    }
}
