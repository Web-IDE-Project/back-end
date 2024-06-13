package sumcoda.webide.entry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.workspace.domain.Workspace;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long>, EntryRepositoryCustom {
    Entry findByNameAndWorkspaceAndIsDirectory(String name, Workspace workspace, Boolean isDirectory);
    List<Entry> findByWorkspace(Workspace workspace);
}
