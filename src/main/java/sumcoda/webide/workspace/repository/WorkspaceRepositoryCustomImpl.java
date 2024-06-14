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
        List<Entry> entries = jpaQueryFactory.selectFrom(entry) // entry 엔티티를 선택
                .where(entry.workspace.id.eq(workspaceId)) // workspaceId와 일치하는 항목을 필터링
                .fetch();

        Set<Long> idSet = new HashSet<>(); // 처리된 ID를 저장할 hashset

        return entries.stream() // entries 목록을 스트림으로 변환
                .map(entry -> WorkspaceEntriesResponseDTO.fromEntity(entry, idSet)) // 각 항목을 DTO로 변환
                .filter(Objects::nonNull) // null이 아닌 항목만 필터링
                .toList();
    }
}
