package sumcoda.webide.entry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.dto.request.EntryCreateRequestDTO;
import sumcoda.webide.entry.dto.request.EntryRenameRequestDTO;
import sumcoda.webide.entry.dto.request.EntrySaveRequestDTO;
import sumcoda.webide.entry.dto.response.EntryCreateResponseDTO;
import sumcoda.webide.entry.dto.response.EntryGetResponseDTO;
import sumcoda.webide.entry.exception.*;
import sumcoda.webide.entry.repository.EntryRepository;
import sumcoda.webide.workspace.domain.Workspace;
import sumcoda.webide.workspace.exception.WorkspaceAccessException;
import sumcoda.webide.workspace.exception.WorkspaceFoundException;
import sumcoda.webide.workspace.repository.WorkspaceRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EntryService {
    private final EntryRepository entryRepository;
    private final WorkspaceRepository workspaceRepository;

    // 파일 생성
    public EntryCreateResponseDTO createFile(Long containerId, Long directoryId, EntryCreateRequestDTO entryCreateRequestDTO, String username) {

        // 워크스페이스가 존재하는지 확인
        Workspace workspace = findWorkspaceById(containerId);

        // 유저가 워크스페이스에 권한이 존재하는지 확인
        checkUserAccessToWorkspace(workspace, username);

        // 워크스페이스 안에 엔트리가 존재하는지 확인
        Entry directory = entryRepository.findByWorkspaceIdAndEntryId(containerId, directoryId)
                .orElseThrow(() -> new EntryFoundException("워크스페이스에 존재하지 않는 파일 Id 입니다.: " + directoryId));

        // 엔트리가 파일인지 확인
        if (!directory.getIsDirectory()) {
            throw new EntryCreateException("파일에 파일을 생성할 수 없습니다.");
        }

        // 디렉토리 안에 같은 이름의 파일이 존재하는지 확인
        if (entryRepository.findByEntryAndName(directory, entryCreateRequestDTO.getName()).isPresent()) {
            throw new EntryAlreadyExistsException("같은 이름의 파일이 이미 존재합니다.: " + entryCreateRequestDTO.getName());
        }

        // 파일 생성
        Entry newFile = Entry.createEntry(
                entryCreateRequestDTO.getName(),
                null,
                false,
                directory,
                workspace
        );

        // 새 파일을 저장
        entryRepository.save(newFile);

        // 응답 DTO 생성 및 반환
        return EntryCreateResponseDTO.builder()
                .id(newFile.getId())
                .name(newFile.getName())
                .isDirectory(newFile.getIsDirectory())
                .parentId(newFile.getParent().getId())
                .build();
    }

    // 공통 메서드

    // 워크스페이스가 존재하는지 확인
    private Workspace findWorkspaceById(Long containerId) {
        return workspaceRepository.findById(containerId)
                .orElseThrow(() -> new WorkspaceFoundException("존재하지 않는 워크스페이스 Id 입니다.: " + containerId));
    }

    // 유저가 워크스페이스에 접근 권한이 존재하는지 확인
    private void checkUserAccessToWorkspace(Workspace workspace, String username) {
        boolean hasAccess = workspace.getMemberWorkspaces().stream()
                .anyMatch(mw -> mw.getMember().getUsername().equals(username));

        // 접근 권한이 없으면 예외 발생
        if (!hasAccess) {
            throw new WorkspaceAccessException("유저는 워크스페이스에 접근 권한이 없습니다.: " + username);
        }
    }
}
