package sumcoda.webide.member.register;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
public class RegisterController {

    // 회원가입 로직 메서드 호출을 위한 필드
    private final RegisterService registerService;

    /**
     * 회원가입 요청 캐치
     *
     * @param registerRequestDTO 프론트로부터 전달받은 회원가입 정보
     **/
    @PostMapping(value = "/api/auth/registerProc")
    public ResponseEntity<?> registerProcess(@RequestBody RegisterRequestDTO registerRequestDTO) {
        log.info("register process is working");

        Map<String, Object> responseData = new HashMap<>();
        try {
            registerService.registerMember(registerRequestDTO);
            RegisterResponseDTO registerResponseDTO =
                    RegisterResponseDTO.builder()
                            .username(registerRequestDTO.getUsername())
                            .nickname(registerRequestDTO.getNickname())
                            .build();

            responseData.put("userInfo", registerResponseDTO);
            responseData.put("message", "회원가입이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("result", "error");
            responseData.put("message", "이미 등록된 아이디 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }
}
