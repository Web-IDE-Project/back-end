package sumcoda.webide.workspace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.chat.domain.ChatMessage;
import sumcoda.webide.chat.domain.ChatRoom;
import sumcoda.webide.chat.repository.ChatMessageRepository;
import sumcoda.webide.chat.repository.ChatRoomRepository;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.exception.RootEntryFoundException;
import sumcoda.webide.entry.repository.EntryRepository;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.exception.MemberFoundException;
import sumcoda.webide.member.repository.MemberRepository;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;
import sumcoda.webide.memberworkspace.enumerate.MemberWorkspaceRole;
import sumcoda.webide.memberworkspace.repository.MemberWorkspaceRepository;
import sumcoda.webide.workspace.domain.Workspace;
import sumcoda.webide.workspace.dto.WorkspacePublicStatusDTO;
import sumcoda.webide.workspace.dto.request.WorkspaceCreateRequestDTO;
import sumcoda.webide.workspace.dto.request.WorkspaceUpdateRequestDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDAO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDTO;
import sumcoda.webide.workspace.enumerate.Category;
import sumcoda.webide.workspace.enumerate.Status;
import sumcoda.webide.workspace.exception.*;
import sumcoda.webide.workspace.repository.WorkspaceRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

    // DB에 정보를 조회, 저장하기 위한 필드
    private final WorkspaceRepository workspaceRepository;

    private final MemberRepository memberRepository;

    private final MemberWorkspaceRepository memberWorkspaceRepository;

    private final EntryRepository entryRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMessageRepository chatMessageRepository;

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
                false,
                Status.DEFAULT
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
    @Transactional
    public WorkspaceEntriesResponseDTO getAllEntriesByWorkspaceId(Long workspaceId, String username) {
        //유저 검증
        validateUserAccess(workspaceId, username);
        //엔트리를 DTO로 변환하여 반환
        return workspaceRepository.findAllEntriesByWorkspaceId(workspaceId);
    }


    /**
     * 워크스페이스의 공개 여부를 확인하고 유저가 워크스페이스에 접근 권한이 있는지 확인하는 메서드
     *
     * @param workspaceId 워크스페이스 ID
     * @param username 사용자명
     **/
    private void validateUserAccess(Long workspaceId, String username) {
        WorkspacePublicStatusDTO workspacePublicStatusDTO = workspaceRepository.findWorkspacePublicInfo(workspaceId);

        // 퍼블릭 워크스페이스 실행 시 유저 검증 없이 바로 실행
        if (workspacePublicStatusDTO.isPublic()) {
            return;
        }

        // 프라이빗 워크스페이스일 때, 유저가 해당 워크스페이스에 접근 권한이 없으면 예외 발생
        if (!workspacePublicStatusDTO.isPublic()) {
            boolean hasAccess = workspaceRepository.hasUserAccess(workspaceId, username);
            if (!hasAccess) {
                throw new WorkspaceAccessException("해당 컨테이너에 접근 권한이 없습니다.: " + username);
            }
        }
    }

    public List<?> getWorkspacesByCategory(Category category, String username) {
        List<WorkspaceResponseDAO> workspaceResponseDAOList = workspaceRepository.findWorkspacesByCategory(category, username);

        if (category == Category.MY) {
            return workspaceResponseDAOList.stream()
                    .map(data -> WorkspaceResponseDTO.My.builder()
                            .id(data.getId())
                            .title(data.getTitle())
                            .language(data.getLanguage().name())
                            .description(data.getDescription())
                            .status(data.getStatus().name())
                            .username(null)
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
                            .status(data.getStatus().name())
                            .username(data.getUsername())
                            .nickname(data.getNickname())
                            .awsS3SavedFileURL(data.getAwsS3SavedFileURL())
                            .build())
                    .toList();
        }
    }

    // 워크 스페이스 수정
    @Transactional
    public void updateWorkspace(Long workspaceId, WorkspaceUpdateRequestDTO workspaceUpdateRequestDTO, String username) {

        // 워크스페이스가 존재하는지 확인
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceFoundException("존재하지 않는 워크스페이스 Id 입니다.: " + workspaceId));

        // 유저가 워크스페이스에 권한이 존재하는지 확인( ADMIN 만 워크스페이스를 수정할 수 있음)
        checkUserAccessToWorkspace(workspace, username);

        // 상태가 DEFAULT 가 아니면 워크스페이스를 수정할 수 없음
        if (workspace.getStatus() != Status.DEFAULT) {
            throw new WorkspaceUpdateException("완료된 컨테이너나 해결된 컨테이너는 수정할 수 없습니다.");
        }

        // 워크스페이스의 제목을 업데이트
        workspace.updateTitle(workspaceUpdateRequestDTO.getTitle());
        // 워크스페이스의 설명을 업데이트
        workspace.updateDescription(workspaceUpdateRequestDTO.getDescription());

        // 입력받은 카테고리
        Category newCategory = workspaceUpdateRequestDTO.getCategory();

        // 나의 -> 강의, 질문 -> 강의, 강의 -> 강의
        // 3가지 경우의 수를 고려

        // 카테고리가 강의 카테고리라면
        if (newCategory == Category.LECTURE) {
            if (!workspace.getCategories().contains(Category.LECTURE)) {

                // 기존에 질문 컨테이너가 있으면 질문 카테고리를 제거
                if(workspace.getCategories().contains(Category.QUESTION)) {
                    workspace.removeCategories(Category.QUESTION);
                }
                // private -> public
                workspace.updateIsPublic(true);
                // 강의 카테고리 추가
                workspace.addCategories(newCategory);

                // 채팅방 생성
                if (workspace.getChatRoom() == null) {
                    ChatRoom chatRoom = ChatRoom.createChatRoom(workspace.getTitle(), workspace);
                    chatRoomRepository.save(chatRoom);
                }
            }

            // 나의 -> 질문, 질문 -> 질문, 강의 -> 질문
            // 3가지 경우의 수를 고려

            // 카테고리가 질문 카테고리라면
        } else if (newCategory == Category.QUESTION) {
            if (!workspace.getCategories().contains(Category.QUESTION)) {

                // 기존에 강의 카테고리가 있으면 강의 카테고리를 제거
                if (workspace.getCategories().contains(Category.LECTURE)) {
                    workspace.removeCategories(Category.LECTURE);
                }
                // private -> public
                workspace.updateIsPublic(true);
                // 질문 카테고리 추가
                workspace.addCategories(newCategory);

                // 채팅방 생성
                if (workspace.getChatRoom() == null) {
                    ChatRoom chatRoom = ChatRoom.createChatRoom(workspace.getTitle(), workspace);
                    chatRoomRepository.save(chatRoom);
                }
            }

            // 나의 -> 나의, 질문 -> 나의, 강의 -> 나의
            // 3가지 경우의 수를 고려

            // 카테고리가 나의 카테고리라면
        } else if (newCategory == Category.MY) {

            // 기존에 강의 또는 질문 카테고리 였으면 강의 또는 질문 카테고리를 제거
            if (workspace.getCategories().contains(Category.LECTURE) || workspace.getCategories().contains(Category.QUESTION)) {
                workspace.updateCategories(Set.of(Category.MY));
            }

            workspace.updateIsPublic(false);
        }
    }

    // 워크 스페이스 상태 수정
    // 강의 -> 완료
    // 질문 -> 해결
    @Transactional
    public void updateWorkspaceStatus(Long workspaceId, Status status, String username) {

        //멤버변수를 사용하기 위한 사용자 검증
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberFoundException("유효하지 않은 사용자입니다."));

        // 워크스페이스가 존재하는지 확인
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceFoundException("존재하지 않는 워크스페이스 Id 입니다.: " + workspaceId));

        // 유저가 워크스페이스에 권한이 존재하는지 확인(ADMIN 만 워크스페이스 상태를 수정할 수 있음)
        checkUserAccessToWorkspace(workspace, username);

        // 상태가 DEFAULT 가 아니면 워크스페이스 상태를 수정할 수 없음
        if (workspace.getStatus() != Status.DEFAULT) {
            throw new WorkspaceStatusException("이미 완료되거나 해결된 컨테이너는 상태를 수정할 수 없습니다.");
        }

        // status 가 COMPLETE 또는 SOLVE 가 아닌 경우 예외를 던짐, 워크스페이스 상태를 DEFAULT 로 수정할 수 없음
        if (status != Status.COMPLETE && status != Status.SOLVE) {
            throw new WorkspaceStatusException("유효하지 않은 상태 값입니다. 상태는 완료 또는 해결이어야 합니다.");
        }

        // 워크스페이스의 카테고리가 QUESTION 또는 LECTURE 인지 확인
        if (!workspace.getCategories().contains(Category.QUESTION) && !workspace.getCategories().contains(Category.LECTURE)) {
            throw new WorkspaceStatusException("워크스페이스가 질문 또는 강의 카테고리를 가져야 상태를 수정할 수 있습니다.");
        }

        // 예외 처리 로직 추가
        if (status == Status.COMPLETE && workspace.getCategories().contains(Category.QUESTION))
            throw new WorkspaceStatusException("질문 컨테이너는 완료 상태가 될 수 없습니다.");
        else if (status == Status.SOLVE && workspace.getCategories().contains(Category.LECTURE)) {
            throw new WorkspaceStatusException("강의 컨테이너는 해결 상태가 될 수 없습니다.");
        }

        // 새로운 워크스페이스 생성
        Workspace newWorkspace = Workspace.createWorkspace(
                workspace.getTitle(),
                status == Status.SOLVE ? Set.of(Category.QUESTION) : Set.of(Category.LECTURE),
                workspace.getLanguage(),
                workspace.getDescription(),
                true,
                status
        );
        workspaceRepository.save(newWorkspace);

        //MemberWorkspace 생성 및 저장
        MemberWorkspace memberWorkspace = MemberWorkspace.createMemberWorkspace(
                MemberWorkspaceRole.ADMIN,
                LocalDateTime.now(),
                member,
                newWorkspace
        );

        memberWorkspaceRepository.save(memberWorkspace);

        // 루트 엔트리 찾기
        Entry rootEntry = workspace.getEntries().stream()
                .filter(entry -> entry.getParent() == null)
                .findFirst()
                .orElseThrow(() -> new RootEntryFoundException("워크스페이스에 최상위 디렉토리가 없습니다: " + workspaceId));

        // 엔트리를 재귀적으로 복사
        copyEntries(rootEntry, null, newWorkspace);

        // 원래 컨테이너 상태로 되돌리기

        // 채팅방 복사 및 기존 채팅방 삭제
        ChatRoom oldChatRoom = workspace.getChatRoom();
        if (oldChatRoom != null) {
            ChatRoom newChatRoom = ChatRoom.createChatRoom(oldChatRoom.getName(), newWorkspace);
            chatRoomRepository.save(newChatRoom);

            // 기존 채팅방의 메시지 복사
            List<ChatMessage> oldMessages = chatMessageRepository.findByChatRoom(oldChatRoom);
            for (ChatMessage oldMessage : oldMessages) {
                ChatMessage newMessage = ChatMessage.createChatMessage(
                        oldMessage.getMessage(),
                        oldMessage.getMessageType(),
                        newChatRoom,
                        oldMessage.getMember()
                );
                chatMessageRepository.save(newMessage);
            }

            chatRoomRepository.delete(oldChatRoom);
        }

        // public -> private
        workspace.updateIsPublic(false);

        // 이미 DEFAULT 상태이므로 로 수정할 필요 없음
//        workspace.updateStatus(Status.DEFAULT);

        // 나의 컨테이너로 카테고리 수정
        workspace.updateCategories(Set.of(Category.MY));
    }

    // 워크스페이스 삭제
    @Transactional
    public void deleteWorkspace(Long workspaceId, String username) {

        // 워크스페이스가 존재하는지 확인
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceFoundException("존재하지 않는 워크스페이스 Id 입니다.: " + workspaceId));

        // 유저가 워크스페이스에 권한이 존재하는지 확인(ADMIN 만 워크스페이스를 삭제할 수 있음)
        checkUserAccessToWorkspace(workspace, username);

        // 상태가 DEFAULT 인 워크스페이스만 삭제할 수 있음
        if (workspace.getStatus() != Status.DEFAULT) {
            throw new WorkspaceStatusException("완료되거나 해결된 컨테이너는 삭제할 수 없습니다.");
        }

        // 워크 스페이스 삭제
        workspaceRepository.delete(workspace);
    }

    // 유저가 워크스페이스에 접근 권한이 존재하는지 확인
    private void checkUserAccessToWorkspace(Workspace workspace, String username) {

        boolean hasAccess = workspace.getMemberWorkspaces().stream()
                .anyMatch(mw -> mw.getMember().getUsername().equals(username) &&
                        mw.getRole().equals(MemberWorkspaceRole.ADMIN));

        // 접근 권한이 없으면 예외 발생
        if (!hasAccess) {
            throw new WorkspaceAccessException("유저는 워크스페이스에 접근 권한이 없습니다.: " + username);
        }
    }

    // 엔트리 복사
    private void copyEntries(Entry entry, Entry parentEntry, Workspace newWorkspace) {
        // 새로운 엔트리를 생성
        Entry newEntry = Entry.createEntry(
                entry.getName(),
                entry.getContent(),
                entry.getIsDirectory(),
                parentEntry,
                newWorkspace
        );
        entryRepository.save(newEntry);

        // 자식 엔트리를 재귀적으로 복사
        for (Entry child : entry.getChildren()) {
            copyEntries(child, newEntry, newWorkspace);
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