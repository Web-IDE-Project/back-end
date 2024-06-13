package sumcoda.webide.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.workspace.domain.Workspace;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
