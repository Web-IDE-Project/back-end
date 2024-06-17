package sumcoda.webide.workspace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.repository.EntryRepository;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.repository.MemberRepository;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;
import sumcoda.webide.memberworkspace.enumerate.MemberWorkspaceRole;
import sumcoda.webide.memberworkspace.repository.MemberWorkspaceRepository;
import sumcoda.webide.workspace.domain.Workspace;
import sumcoda.webide.workspace.dto.WorkspaceAccessDTO;
import sumcoda.webide.workspace.dto.request.WorkspaceCreateRequestDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDAO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDTO;
import sumcoda.webide.workspace.enumerate.Category;
import sumcoda.webide.workspace.exception.WorkspaceAccessException;
import sumcoda.webide.workspace.exception.WorkspaceNotCreateException;
import sumcoda.webide.workspace.repository.WorkspaceRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

    // DB에 정보를 조회, 저장하기 위한 필드
    private final WorkspaceRepository workspaceRepository;

    private final MemberRepository memberRepository;

    private final MemberWorkspaceRepository memberWorkspaceRepository;

    private final EntryRepository entryRepository;

    /**
     * 워크스페이스 생성 요청 캐치
     *
     * @param workspaceCreateRequestDTO Controller 에서 전달받은 워크스페이스 정보
     **/
    @Transactional
    public Long createWorkspace(WorkspaceCreateRequestDTO workspaceCreateRequestDTO, String username) {
        //멤버변수를 사용하기 위한 사용자 검증
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // title == null이면 예외 발생
        if (workspaceCreateRequestDTO.getTitle() == null)
        {
            throw new WorkspaceNotCreateException("컨테이너 이름을 입력해주세요.");
        }

        // language == null이면 예외 발생
        if (workspaceCreateRequestDTO.getLanguage() == null)
        {
            throw new WorkspaceNotCreateException("언어를 선택해주세요.");
        }

        //워크스페이스 생성 및 저장
        //Category 값과 isPublic 값은 디폴트 값으로 저장
        Workspace workspace = Workspace.createWorkspace(
                workspaceCreateRequestDTO.getTitle(),
                new HashSet<>(Arrays.asList(Category.MY)),
                workspaceCreateRequestDTO.getLanguage(),
                workspaceCreateRequestDTO.getDescription(),
                false
        );

        workspaceRepository.save(workspace);

        //MemberWorkspace 생성 및 저장
        MemberWorkspace memberWorkspace = MemberWorkspace.createMemberWorkspace(
                MemberWorkspaceRole.ADMIN,
                LocalDateTime.now(),
                member,
                workspace
        );

        memberWorkspaceRepository.save(memberWorkspace);

        //최상위 디렉토리 생성 및 저장
        Entry rootDirectory = Entry.createEntry(
                "WORKSPACE-" + UUID.randomUUID().toString(),
                null,
                true,
                null,
                workspace
        );

        entryRepository.save(rootDirectory);

        //기본 템플릿 파일 생성 및 저장
        String templateContent = String.format(sumcoda.webide.workspace.template.BasicTemplate.getTemplate(workspaceCreateRequestDTO.getLanguage().toString()), "Hello, World!");
        Entry templateFile = Entry.createEntry(
                getTemplateFileName(workspaceCreateRequestDTO.getLanguage().toString()),
                templateContent,
                false,
                rootDirectory,
                workspace
        );

        entryRepository.save(templateFile);

        return workspace.getId();
    }


    /**
     * 워크스페이스 실행 요청 캐치
     *
     * @param workspaceId Controller 에서 전달받은 워크스페이스 id
     **/
    //워크스페이스 실행
    public WorkspaceEntriesResponseDTO getAllEntriesByWorkspaceId(Long workspaceId, String username) {
        //유저 검증
        validateUserAccess(workspaceId, username);
        //엔트리를 DTO로 변환하여 반환
        return workspaceRepository.findAllEntriesByWorkspaceId(workspaceId);
    }


    /**
     * 유저가 워크스페이스에 접근 권한이 있는지 확인하는 메서드
     *
     * @param workspaceId 워크스페이스 ID
     * @param username 사용자명
     **/
    private void validateUserAccess(Long workspaceId, String username) {
        WorkspaceAccessDTO workspaceAccessDTO = workspaceRepository.findWorkspaceAccessInfo(workspaceId, username);

        /* 유저가 해당 워크스페이스에 접근 권한이 없거나
        private 워크스페이스에 admin이 아닌 유저가 접근하려고 하면 예외 발생 */
        if (Boolean.TRUE.equals(!workspaceRepository.hasUserAccess(workspaceId, username)) ||
                (!workspaceAccessDTO.isPublic() && workspaceAccessDTO.getRole() != MemberWorkspaceRole.ADMIN))
        {
            throw new WorkspaceAccessException("해당 컨테이너에 접근 권한이 없습니다.: " + username);
        }
    }

    @Transactional(readOnly = true)
    public List<?> getWorkspacesByCategory(Category category, String username) {
        List<WorkspaceResponseDAO> workspaceResponseDAOList = workspaceRepository.findWorkspacesByCategory(category, username);

        if (category == Category.MY) {
            return workspaceResponseDAOList.stream()
                    .map(data -> WorkspaceResponseDTO.My.builder()
                            .id(data.getId())
                            .title(data.getTitle())
                            .language(data.getLanguage().name())
                            .description(data.getDescription())
                            .nickname(null)
                            .awsS3SavedFileURL(data.getAwsS3SavedFileURL())
                            .build())
                    .toList();
        } else {
            return workspaceResponseDAOList.stream()
                    .map(data -> WorkspaceResponseDTO.builder()
                            .id(data.getId())
                            .title(data.getTitle())
                            .language(data.getLanguage().name())
                            .description(data.getDescription())
                            .nickname(data.getNickname())
                            .awsS3SavedFileURL(data.getAwsS3SavedFileURL())
                            .build())
                    .toList();
        }
    }

    //기본 템플릿 파일 이름 설정
    private String getTemplateFileName(String language) {
        return switch (language) {
            case "C" -> "main.c";
            case "CPP" -> "main.cpp";
            case "JAVA" -> "Main.java";
            case "JAVASCRIPT" -> "main.js";
            case "PYTHON" -> "main.py";
            default -> "main.txt";
        };
    }
}