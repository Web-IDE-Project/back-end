package sumcoda.webide.entry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sumcoda.webide.entry.domain.Entry;

import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long>, EntryRepositoryCustom {

    // workspaceId 와 entryId 를 비교하여 해당하는 Entry 엔티티를 조회하는 메서드
    Optional<Entry> findByWorkspaceIdAndId(Long workspaceId, Long entryId);

    // parentEntry 와 name 을 비교하여 해당하는 Entry 엔티티를 조회하는 메서드
    Optional<Entry> findByParentAndName(Entry parent, String name);
}
