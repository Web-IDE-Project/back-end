package sumcoda.webide.workspace.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static sumcoda.webide.entry.domain.QEntry.*;

@RequiredArgsConstructor
public class WorkspaceRepositoryCustomImpl implements WorkspaceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<WorkspaceEntriesResponseDTO> findAllEntriesByWorkspaceId(Long workspaceId) {
        List<Entry> entries = jpaQueryFactory.selectFrom(entry)
                .where(entry.workspace.id.eq(workspaceId))
                .fetch();

        Set<Long> idSet = new HashSet<>();

        return entries.stream()
                .map(entry -> WorkspaceEntriesResponseDTO.fromEntity(entry, idSet))
                .filter(Objects::nonNull)
                .toList();
    }
}
