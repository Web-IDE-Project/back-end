package sumcoda.webide.workspace.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.memberworkspace.enumerate.MemberWorkspaceRole;
import sumcoda.webide.workspace.dto.WorkspaceAccessDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDAO;
import sumcoda.webide.workspace.enumerate.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static sumcoda.webide.entry.domain.QEntry.*;
import static sumcoda.webide.member.domain.QMember.member;
import static sumcoda.webide.member.domain.QProfileImage.*;
import static sumcoda.webide.memberworkspace.domain.QMemberWorkspace.memberWorkspace;
import static sumcoda.webide.workspace.domain.QWorkspace.workspace;
@Slf4j
@RequiredArgsConstructor
public class WorkspaceRepositoryCustomImpl implements WorkspaceRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public WorkspaceEntriesResponseDTO findAllEntriesByWorkspaceId(Long workspaceId) {
        List<Entry> entries = jpaQueryFactory.selectFrom(entry) // entry 엔티티를 선택
                .where(entry.workspace.id.eq(workspaceId)) // workspaceId와 일치하는 항목을 필터링
                .fetch();

        Set<Long> idSet = new HashSet<>(); // 처리된 ID를 저장할 hashset

        return entries.stream() // entries 목록을 스트림으로 변환
                .map(entry -> WorkspaceEntriesResponseDTO.fromEntity(entry, idSet)) // 각 항목을 DTO로 변환
                .filter(Objects::nonNull) // null이 아닌 항목만 필터링
                .toList().get(0);
    }

    /**
     * 워크스페이스 공개 여부와 역할을 통해 접근 권한이 있는지 확인하는 메서드
     *
     * @param workspaceId 워크스페이스 ID
     * @param username 사용자명
     * @return 워크스페이스 접근 정보를 담은 DTO
     **/
    @Override
    public WorkspaceAccessDTO findWorkspaceAccessInfo(Long workspaceId, String username) {
        return jpaQueryFactory.select(Projections.fields(WorkspaceAccessDTO.class,
                        memberWorkspace.role,
                        workspace.isPublic))
                .from(workspace)
                .leftJoin(workspace.memberWorkspaces, memberWorkspace) // 워크스페이스와 연관된 멤버워크스페이스 조인
                .leftJoin(memberWorkspace.member, member) // 멤버워크스페이스와 연관된 멤버 조인
                .where(workspace.id.eq(workspaceId) // 워크스페이스 id가 일치하고
                        .and(member.username.eq(username))) // 멤버 username이 일치하는 조건
                .fetchOne();
    }

    /**
     * 사용자가 주어진 워크스페이스에 접근 권한이 있는지 확인하는 메서드
     *
     * @param workspaceId 워크스페이스 ID
     * @param username 사용자명
     * @return 사용자가 접근 권한이 있는지 여부
     **/
    @Override
    public Boolean hasUserAccess(Long workspaceId, String username) {
        Integer count = jpaQueryFactory.selectOne()  // 단일 결과를 선택
                .from(workspace)
                .leftJoin(workspace.memberWorkspaces, memberWorkspace)
                .leftJoin(memberWorkspace.member, member)
                .where(workspace.id.eq(workspaceId)
                        .and(member.username.eq(username)))
                .fetchFirst(); // 첫 번째 결과를 반환

        return count != null; // count가 null이 아니면 true 반환
    }

    @Override
    public List<WorkspaceResponseDAO> findWorkspacesByCategory(Category category, String username) {

        BooleanExpression whereClause = whereClause(category, username);

        return jpaQueryFactory.select(Projections.fields(WorkspaceResponseDAO.class,
                        workspace.id,
                        workspace.title,
                        workspace.language,
                        workspace.description,
                        member.nickname,
                        profileImage.awsS3SavedFileURL
                ))
                .from(workspace)
                .leftJoin(workspace.memberWorkspaces, memberWorkspace)
                .leftJoin(memberWorkspace.member, member)
                .leftJoin(member.profileImage, profileImage)
                .where(whereClause)
                .fetch();
    }

    // 동적으로 where 조건을 설정할 수 있는 메서드
    private BooleanExpression whereClause(Category category, String username) {
        BooleanExpression whereClause = workspace.categories.contains(category);

        if (category == Category.MY) {
            whereClause = whereClause.and(member.username.eq(username))
                    .and(memberWorkspace.role.eq(MemberWorkspaceRole.ADMIN));
        }

        return whereClause;
    }
}
