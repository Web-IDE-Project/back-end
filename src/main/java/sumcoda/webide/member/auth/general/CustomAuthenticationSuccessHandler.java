package sumcoda.webide.member.auth.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sumcoda.webide.member.dto.AuthResponseDTO;
import sumcoda.webide.member.dto.MemberResponseDTO;
import sumcoda.webide.member.repository.MemberRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();

        log.info("success handler is working");
        UserDetails user =  (UserDetails) authentication.getPrincipal();
        String username = user.getUsername();
        log.info("login user name : " + username);

        MemberResponseDTO memberResponseDTO = memberRepository.findOneByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        AuthResponseDTO authResponseDTO =
                AuthResponseDTO.builder()
                        .username(memberResponseDTO.getUsername())
                        .nickname(memberResponseDTO.getNickname())
                        .awsS3SavedFileURL(memberResponseDTO.getAwsS3SavedFileURL())
                        .build();

        responseData.put("userInfo", authResponseDTO);
        responseData.put("message", "로그인에 성공하였습니다.");

        objectMapper.writeValue(response.getWriter(), responseData);
    }
}
