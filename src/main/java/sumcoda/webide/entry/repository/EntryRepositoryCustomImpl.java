package sumcoda.webide.entry.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sumcoda.webide.entry.domain.Entry;

import java.util.Optional;

import static sumcoda.webide.entry.domain.QEntry.entry;

@Repository
@RequiredArgsConstructor
public class EntryRepositoryCustomImpl implements EntryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // workspaceId 와 entryId 를 비교하여 해당하는 Entry 엔티티를 조회하는 메서드
    @Override
    public Optional<Entry> findByWorkspaceIdAndEntryId(Long workspaceId, Long entryId) {
        return Optional.ofNullable(queryFactory.selectFrom(entry)
                .where(
                        entry.workspace.id.eq(workspaceId),
                        entry.id.eq(entryId)
                )
                .fetchOne());
    }

    // parentEntry 와 name 을 비교하여 해당하는 Entry 엔티티를 조회하는 메서드
    @Override
    public Optional<Entry> findByEntryAndName(Entry parentEntry, String name) {
        return Optional.ofNullable(queryFactory.selectFrom(entry)
                .where(
                        entry.parent.eq(parentEntry),
                        entry.name.eq(name)
                )
                .fetchOne());
    }
}
