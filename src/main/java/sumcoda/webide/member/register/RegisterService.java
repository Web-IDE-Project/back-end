package sumcoda.webide.member.register;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.enumerate.Role;
import sumcoda.webide.member.exception.UserAlreadyExistsException;
import sumcoda.webide.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterService {

    // DB에 회원가입 정보를 조회, 저장하기 위한 필드
    private final MemberRepository memberRepository;

    // 회원가입시 비밀번호를 암호화 하기 위한 필드
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입 요청 캐치
     *
     * @param registerRequestDTO Controller 에서 전달받은 회원가입 정보
     **/
    @Transactional
    public void registerMember(RegisterRequestDTO registerRequestDTO) throws Exception {
        Boolean isAlreadyUser = memberRepository.existsByUsername(registerRequestDTO.getUsername());

        if (Boolean.TRUE.equals(isAlreadyUser)) {
            throw new UserAlreadyExistsException("동일한 아이디가 이미 존재합니다: " + registerRequestDTO.getUsername());
        }

        memberRepository.save(Member.createMember(
                registerRequestDTO.getUsername(),
                bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()),
                registerRequestDTO.getNickname(),
                registerRequestDTO.getEmail(),
                Role.USER));
    }
}
