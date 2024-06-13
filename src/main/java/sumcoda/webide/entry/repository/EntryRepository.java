package sumcoda.webide.entry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sumcoda.webide.entry.domain.Entry;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
}
