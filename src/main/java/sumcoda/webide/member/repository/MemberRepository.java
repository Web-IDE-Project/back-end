package sumcoda.webide.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.member.domain.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);
}
