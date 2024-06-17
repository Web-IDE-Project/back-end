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

    @Override
    public Optional<EntryResponseDTO> findRootByWorkspaceIdDTO(Long workspaceId){
        EntryResponseDTO rootEntry = jpaQueryFactory
                .select(Projections.fields(EntryResponseDTO.class,
                        entry.id,
                        entry.name,
                        entry.isDirectory,
                        entry.content,
                        entry.parent.id))
                .from(entry)
                .where(entry.parent.isNull()
                        .and(entry.isDirectory.isTrue())
                        .and(entry.workspace.id.eq(workspaceId)))
                .fetchOne();

        return Optional.ofNullable(rootEntry);
    }

    @Override
    public Optional<Entry> findRootByWorkspaceIdEntity(Long workspaceId){
        Entry rootEntry = jpaQueryFactory
                .select(entry)
                .from(entry)
                .where(entry.parent.isNull()
                        .and(entry.isDirectory.isTrue())
                        .and(entry.workspace.id.eq(workspaceId)))
                .fetchOne();

        return Optional.ofNullable(rootEntry);
    }

    @Override
    public Optional<EntryResponseDTO> findByPathDTO(Long workspaceId, String path) {
        EntryResponseDTO currentEntry = findRootByWorkspaceIdDTO(workspaceId).orElse(null);
        if (currentEntry == null) {
            return Optional.empty();
        }

        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            currentEntry = jpaQueryFactory
                    .select(Projections.fields(EntryResponseDTO.class,
                            entry.id,
                            entry.name,
                            entry.isDirectory,
                            entry.content,
                            entry.parent.id))
                    .from(entry)
                    .where(entry.name.eq(part)
                            .and(entry.parent.id.eq(currentEntry.getId()))
                            .and(entry.workspace.id.eq(workspaceId)))
                    .fetchOne();

            if (currentEntry == null) {
                return Optional.empty();
            }
        }

        return Optional.ofNullable(currentEntry);
    }

    @Override
    public Optional<Entry> findByPathEntity(Long workspaceId, String path) {
        Entry currentEntry = findRootByWorkspaceIdEntity(workspaceId).orElse(null);
        if (currentEntry == null) {
            return Optional.empty();
        }

        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            currentEntry = jpaQueryFactory
                    .select(entry)
                    .from(entry)
                    .where(entry.name.eq(part)
                            .and(entry.parent.id.eq(currentEntry.getId()))
                            .and(entry.workspace.id.eq(workspaceId)))
                    .fetchOne();

            if (currentEntry == null) {
                return Optional.empty();
            }
        }

        return Optional.ofNullable(currentEntry);
    }

    @Override
    public List<EntryResponseDTO> findChildren(Long parentId) {
        return jpaQueryFactory
                .select(Projections.fields(EntryResponseDTO.class,
                        entry.id,
                        entry.name,
                        entry.isDirectory,
                        entry.content,
                        entry.parent.id))
                .from(entry)
                .where(entry.parent.id.eq(parentId))
                .fetch();
    }
}
