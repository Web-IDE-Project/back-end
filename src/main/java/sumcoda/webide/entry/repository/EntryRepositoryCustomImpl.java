package sumcoda.webide.entry.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.dto.response.EntryResponseDTO;

import java.util.List;
import java.util.Optional;

import static sumcoda.webide.entry.domain.QEntry.entry;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EntryRepositoryCustomImpl implements EntryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 주어진 워크스페이스 ID로 루트 엔트리를 찾음
     *
     * @param workspaceId 워크스페이스 ID
     * @return 루트 엔트리 객체
     */
    @Override
    public Optional<Entry> findRootByWorkspaceId(Long workspaceId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(entry)
                .where(entry.workspace.id.eq(workspaceId)
                        .and(entry.parent.isNull()))
                .fetchOne());
    }

    /**
     * 주어진 경로로 엔트리를 찾음
     *
     * @param workspaceId 워크스페이스 ID
     * @param path 엔트리 경로
     * @return 경로에 해당하는 엔트리 객체
     */
    @Override
    public Optional<Entry> findByPath(Long workspaceId, String path) {
        // 이 메서드는 QueryDSL을 사용하여 경로를 기반으로 엔트리를 검색하는 로직을 구현해야 한다.
        // 예를 들어, 경로를 '/'로 구분하여 단계적으로 엔트리를 검색할 수 있다.
        String[] parts = path.split("/");
        Entry currentEntry = jpaQueryFactory
                .selectFrom(entry)
                .where(entry.workspace.id.eq(workspaceId)
                        .and(entry.parent.isNull())
                        .and(entry.name.eq(parts[1])))
                .fetchOne();
        if (currentEntry == null) {
            return Optional.empty();
        }
        for (int i = 2; i < parts.length; i++) {
            currentEntry = jpaQueryFactory
                    .selectFrom(entry)
                    .where(entry.workspace.id.eq(workspaceId)
                            .and(entry.parent.eq(currentEntry))
                            .and(entry.name.eq(parts[i])))
                    .fetchOne();
            if (currentEntry == null) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(currentEntry);
    }

    /**
     * 주어진 워크스페이스 ID로 루트 엔트리를 찾음
     *
     * @param workspaceId 워크스페이스 ID
     * @return 루트 엔트리를 나타내는 EntryResponseDTO 객체
     */
    @Override
    public Optional<EntryResponseDTO> findRootByWorkspaceIdDTO(Long workspaceId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.fields(EntryResponseDTO.class,
                        entry.id,
                        entry.name,
                        entry.content,
                        entry.isDirectory,
                        entry.parent.id,
                        entry.workspace.id))
                .from(entry)
                .where(entry.workspace.id.eq(workspaceId)
                        .and(entry.parent.isNull()))
                .fetchOne());
    }

    @Override
    public Optional<EntryResponseDTO> findByWorkspaceIdAndParentIdAndNameDTO(Long workspaceId, Long parentId, String name) {

        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.bean(EntryResponseDTO.class,
                        entry.id,
                        entry.name,
                        entry.isDirectory,
                        entry.parent.id,
                        entry.workspace.id))
                .from(entry)
                .where(entry.workspace.id.eq(workspaceId)
                        .and(entry.parent.id.eq(parentId))
                        .and(entry.name.eq(name)))
                .fetchOne());

    }

    /**
     * 주어진 부모 ID로 자식 엔트리 목록을 찾음
     *
     * @param parentId 부모 엔트리 ID
     * @return 자식 엔트리 목록을 나타내는 EntryResponseDTO 리스트
     */
    @Override
    public List<EntryResponseDTO> findChildrenDTO(Long parentId) {
        return jpaQueryFactory
                .select(Projections.fields(EntryResponseDTO.class,
                        entry.id,
                        entry.name,
                        entry.content,
                        entry.isDirectory,
                        entry.parent.id,
                        entry.workspace.id))
                .from(entry)
                .where(entry.parent.id.eq(parentId))
                .fetch();
    }

}
