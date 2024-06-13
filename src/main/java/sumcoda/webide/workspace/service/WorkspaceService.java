package sumcoda.webide.workspace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.repository.MemberRepository;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;
import sumcoda.webide.memberworkspace.repository.MemberWorkspaceRepository;
import sumcoda.webide.workspace.domain.Workspace;
import sumcoda.webide.workspace.dto.request.WorkspaceCreateRequestDTO;
import sumcoda.webide.workspace.enumerate.Category;
import sumcoda.webide.workspace.repository.WorkspaceRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

    // DB에 정보를 조회, 저장하기 위한 필드
    private final WorkspaceRepository workspaceRepository;
    private final MemberRepository memberRepository;
    private final MemberWorkspaceRepository memberWorkspaceRepository;

    /**
     * 워크스페이스 생성 요청 캐치
     *
     * @param workspaceCreateRequestDTO Controller 에서 전달받은 워크스페이스 정보
     **/
    @Transactional
    public void createWorkspace(WorkspaceCreateRequestDTO workspaceCreateRequestDTO, String username) {
        //멤버변수를 사용하기 위한 사용자 검증
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        //워크스페이스 생성 및 저장
        //Category 값과 isPublic 값은 디폴트 값으로 저장
        Workspace workspace = Workspace.createWorkspace(
                workspaceCreateRequestDTO.getTitle(),
                Category.MY,
                workspaceCreateRequestDTO.getLanguage(),
                workspaceCreateRequestDTO.getDescription(),
                "WORKSPACE-" + UUID.randomUUID().toString(),
                false
        );
        workspaceRepository.save(workspace);

        //MemberWorkspace 생성 및 저장
        MemberWorkspace memberWorkspace = MemberWorkspace.createMemberWorkspace(
                null,
                LocalDateTime.now(),
                member,
                workspace
        );
        memberWorkspaceRepository.save(memberWorkspace);
    }
}
