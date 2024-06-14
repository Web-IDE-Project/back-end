package sumcoda.webide.workspace.repository;

import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;

import java.util.List;

public interface WorkspaceRepositoryCustom {

    List<WorkspaceEntriesResponseDTO> findAllEntriesByWorkspaceId(Long workspaceId);
}
