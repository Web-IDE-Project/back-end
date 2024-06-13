package sumcoda.webide.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import sumcoda.webide.workspace.domain.Workspace;

@Repository
public interface WorkspaceRepository extends JpaRepository <Workspace, Long> {
  
}
