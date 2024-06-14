package sumcoda.webide.workspace.repository;

import sumcoda.webide.workspace.dto.response.WorkspaceResponseDTO;

import java.util.List;

public interface WorkspaceRepositoryCustom {

    List<WorkspaceResponseDTO> findWorkspaceResponseDTOsByWorkspace(Long workspaceId);
}
