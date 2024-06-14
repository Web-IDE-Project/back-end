package sumcoda.webide.workspace.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.domain.QEntry;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WorkspaceRepositoryCustomImpl implements WorkspaceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<WorkspaceResponseDTO> findWorkspaceResponseDTOsByWorkspace(Long workspaceId) {
        // QueryDSL을 사용하기 위한 QEntry 객체 생성
        QEntry qEntry = QEntry.entry;

        // 주어진 워크스페이스 ID에 해당하는 모든 엔트리를 조회
        List<Entry> allEntries = jpaQueryFactory
                .selectFrom(qEntry)
                // 조건: 워크스페이스 ID가 일치하는 엔트리
                .where(qEntry.workspace.id.eq(workspaceId))
                .fetch();

        // 최상위 엔트리만 필터링
        List<Entry> topEntries = allEntries.stream()
                // 부모가 없는 엔트리만 필터링
                .filter(entry -> entry.getParent() == null)
                .collect(Collectors.toList());

        // 최상위 엔트리들을 WorkspaceResponseDTO로 변환하여 반환
        return topEntries.stream()
                .map(entry -> toWorkspaceResponseDTO(entry, allEntries))
                .collect(Collectors.toList());
    }

    // Entry 엔티티를 WorkspaceResponseDTO로 변환하는 메서드
    private WorkspaceResponseDTO toWorkspaceResponseDTO(Entry entry, List<Entry> allEntries) {
        // 현재 엔트리의 하위 엔트리를 필터링
        List<Entry> childrenEntries = allEntries.stream()
                // 부모가 현재 엔트리인 하위 엔트리만 필터링
                .filter(e -> e.getParent() != null && e.getParent().getId().equals(entry.getId()))
                .collect(Collectors.toList());

        //하위 엔트리들을 DTO로 변환
        List<WorkspaceResponseDTO> children = childrenEntries.isEmpty() ? null :
                childrenEntries.stream()
                        // 각 하위 엔트리를 DTO로 변환
                        .map(childEntry -> toWorkspaceResponseDTO(childEntry, allEntries))
                        .collect(Collectors.toList());

        // WorkspaceResponseDTO 객체를 빌더로 생성하여 반환
        return WorkspaceResponseDTO.builder()
                .id(entry.getId())
                .name(entry.getName())
                .isDirectory(entry.getIsDirectory())
                .content(entry.getContent())
                .children(children)
                .build();
    }
}
