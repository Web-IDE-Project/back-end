package sumcoda.webide.memberworkspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;

@Repository
public interface MemberWorkspaceRepository extends JpaRepository<MemberWorkspace, Long> {
}
