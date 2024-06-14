package sumcoda.webide.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.member.auth.social.CustomOAuth2User;
import sumcoda.webide.member.dto.UpdateMemberRequestDTO;
import sumcoda.webide.member.serivce.MemberService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    // 워크스페이스 생성 로직 메서드 호출을 위한 필드
    private final MemberService memberService;

    /**
     * 멤버 정보 수정 요청 캐치
     *
     * @param updateMemberRequestDTO 프론트로부터 전달받은 새로운 닉네임, 비밀번호 정보
     * @param authentication            인증 정보
     */
    @PutMapping
    public ResponseEntity<?> updateMemberInfos(
            @RequestBody UpdateMemberRequestDTO updateMemberRequestDTO,
            Authentication authentication) {

        String username;

        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        // 사용자 정보를 업데이트하는 서비스 메서드 호출
        memberService.updateMemberInfos(username, updateMemberRequestDTO);

        // 응답 데이터를 저장할 hashmap 생성
        Map<String, Object> responseData = new HashMap<>();
        try {
            // 사용자 정보를 업데이트하는 서비스 메서드 호출
            memberService.updateMemberInfos(username, updateMemberRequestDTO);
            responseData.put("message", "수정되었습니다.");
            return ResponseEntity.status(HttpStatus.OK).body(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("result", "error");
            responseData.put("message", "수정이 정상적으로 완료되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }
    }
}
