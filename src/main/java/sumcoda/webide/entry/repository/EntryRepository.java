package sumcoda.webide.entry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumcoda.webide.entry.domain.Entry;

public interface EntryRepository extends JpaRepository<Entry, Long>, EntryRepositoryCustom {
}
