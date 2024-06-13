package sumcoda.webide.entry.repository;

import sumcoda.webide.entry.domain.Entry;

import java.util.Optional;

public interface EntryRepositoryCustom {
    Optional<Entry> findByWorkspaceIdAndEntryId(Long workspaceId, Long entryId);
    Optional<Entry> findByEntryAndName(Entry parentDirectory, String name);
}
