package sumcoda.webide.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Boolean existsByUsername(String username);
}
