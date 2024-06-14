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
}
