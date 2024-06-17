package sumcoda.webide.entry.repository;

import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.dto.response.EntryResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EntryRepositoryCustom {

    Optional<Entry> findRootByWorkspaceId(Long workspaceId);

    Optional<Entry> findByPath(Long workspaceId, String path);

    Optional<EntryResponseDTO> findRootByWorkspaceIdDTO(Long workspaceId);

    List<EntryResponseDTO> findChildrenDTO(Long parentId);
}
