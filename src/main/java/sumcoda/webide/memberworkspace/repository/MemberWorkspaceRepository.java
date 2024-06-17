package sumcoda.webide.memberworkspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;

import java.util.Optional;

@Repository
public interface MemberWorkspaceRepository extends JpaRepository<MemberWorkspace, Long> {

    Optional<MemberWorkspace> findByUsernameAndWorkspaceId(String username, Long workspaceId);
}
