package sumcoda.webide.workspace.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDAO;
import sumcoda.webide.workspace.enumerate.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static sumcoda.webide.entry.domain.QEntry.*;
import static sumcoda.webide.member.domain.QMember.member;
import static sumcoda.webide.memberworkspace.domain.QMemberWorkspace.memberWorkspace;
import static sumcoda.webide.workspace.domain.QWorkspace.workspace;
@Slf4j
@RequiredArgsConstructor
public class WorkspaceRepositoryCustomImpl implements WorkspaceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<WorkspaceEntriesResponseDTO> findAllEntriesByWorkspaceId(Long workspaceId) {
        List<Entry> entries = jpaQueryFactory.selectFrom(entry)
                .where(entry.workspace.id.eq(workspaceId))
                .fetch();

        Set<Long> idSet = new HashSet<>();

        return entries.stream()
                .map(entry -> WorkspaceEntriesResponseDTO.fromEntity(entry, idSet))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<WorkspaceResponseDAO> findWorkspacesByCategory(Category category) {
        return jpaQueryFactory.select(Projections.fields(WorkspaceResponseDAO.class,
                        workspace.id,
                        workspace.title,
                        workspace.language,
                        workspace.description,
                        member.nickname))
                .from(workspace)
                .leftJoin(workspace.memberWorkspaces, memberWorkspace)
                .leftJoin(memberWorkspace.member, member)
                .where(workspace.categories.contains(category))
                .fetch();
    }
}
