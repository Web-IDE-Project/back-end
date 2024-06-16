package sumcoda.webide.member.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.member.dto.AuthResponseDTO;
import sumcoda.webide.member.auth.register.RegisterRequestDTO;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.dto.MemberResponseDTO;
import sumcoda.webide.member.dto.ValidatePasswordRequestDTO;
import sumcoda.webide.member.enumerate.Role;
import sumcoda.webide.member.exception.UserAlreadyExistsException;
import sumcoda.webide.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

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
    public void registerMember(RegisterRequestDTO registerRequestDTO) {
        Boolean isAlreadyUser = memberRepository.existsByUsername(registerRequestDTO.getUsername());

        if (Boolean.TRUE.equals(isAlreadyUser)) {
            throw new UserAlreadyExistsException("동일한 아이디가 이미 존재합니다: " + registerRequestDTO.getUsername());
        }

        memberRepository.save(Member.createMember(
                        registerRequestDTO.getUsername(),
                        bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()),
                        registerRequestDTO.getNickname(),
                        registerRequestDTO.getEmail(),
                        Role.USER,
                null));
    }

    /**
     * 아이디를 바탕으로 멤버 조회
     *
     * @param username 조회할 유저의 ID
     **/
    public AuthResponseDTO findOneByUsername(String username) {
        MemberResponseDTO memberResponseDTO = memberRepository.findOneByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        return AuthResponseDTO.builder()
                .username(memberResponseDTO.getUsername())
                .nickname(memberResponseDTO.getNickname())
                .awsS3SavedFileURL(memberResponseDTO.getAwsS3SavedFileURL())
                .build();
    }

    /**
     * 입력된 비밀번호가 현재 로그인된 사용자의 비밀번호와 일치하는지 확인하는 메서드
     * @param username 현재 로그인된 사용자의 사용자명
     * @param validatePasswordRequestDTO 입력된 비밀번호가 저장된 DTO
     * @return 비밀번호가 일치하면 true, 그렇지 않으면 false
     */
    public Boolean validatePassword(String username, ValidatePasswordRequestDTO validatePasswordRequestDTO) {
        MemberResponseDTO memberResponseDTO = memberRepository.findOneByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        // DB에 저장된 암호화된 비밀번호와 입력된 비밀번호를 비교
        boolean isValidate = bCryptPasswordEncoder.matches(validatePasswordRequestDTO.getPassword(), memberResponseDTO.getPassword());

        if (!isValidate) {
            throw new ValidationFailureException("입력하신 비밀번호가 올바르지 않습니다.");
        }

        return true;
    }

}
