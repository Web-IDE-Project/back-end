package sumcoda.webide.workspace.repository;

import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDAO;
import sumcoda.webide.workspace.enumerate.Category;

import java.util.List;

public interface WorkspaceRepositoryCustom {

    WorkspaceEntriesResponseDTO findAllEntriesByWorkspaceId(Long workspaceId);

    List<WorkspaceResponseDAO> findWorkspacesByCategory(Category category, String username);
}
