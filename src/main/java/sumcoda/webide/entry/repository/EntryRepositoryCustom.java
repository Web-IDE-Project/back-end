package sumcoda.webide.entry.repository;

import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.dto.response.EntryResponseDTO;

import java.util.List;
import java.util.Optional;

public interface EntryRepositoryCustom {
    Optional<EntryResponseDTO> findRootByWorkspaceIdDTO(Long workspaceId);

    Optional<Entry> findRootByWorkspaceIdEntity(Long workspaceId);
    Optional<EntryResponseDTO> findByPathDTO(Long workspaceId, String path);

    // 주어진 경로와 워크스페이스 ID에 해당하는 Entry를 찾음
    Optional<Entry> findByPathEntity(Long workspaceId, String path);
    List<EntryResponseDTO> findChildren(Long parentId);
}
