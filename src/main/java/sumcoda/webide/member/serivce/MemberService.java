package sumcoda.webide.member.serivce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.dto.UpdateMemberRequestDTO;
import sumcoda.webide.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    // 멤버를 조회하기 위한 필드
    private final MemberRepository memberRepository;

    @Transactional
    public void updateMemberInfos(String username, UpdateMemberRequestDTO updateMemberRequestDTO) {
        // 사용자 이름으로 멤버 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 닉네임이 null이 아니면 닉네임 업데이트
        if (updateMemberRequestDTO.getNickname() != null) {
            member.assignNickname(updateMemberRequestDTO.getNickname());
        }

        // 비밀번호가 null이 아니면 비밀번호 업데이트
        if (updateMemberRequestDTO.getPassword() != null) {
            member.assignPassword(updateMemberRequestDTO.getPassword());
        }
    }
}
