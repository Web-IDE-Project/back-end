package sumcoda.webide.member.repository;

import sumcoda.webide.member.dto.MemberResponseDTO;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<MemberResponseDTO> findOneByUsername(String username);
}
