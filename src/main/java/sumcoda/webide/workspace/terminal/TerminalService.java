/// src/main/java/com/example/service/TerminalService.java

package sumcoda.webide.workspace.terminal;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.entry.dto.response.EntryResponseDTO;
import sumcoda.webide.entry.repository.EntryRepository;
import sumcoda.webide.entry.service.CompileService;
import sumcoda.webide.workspace.domain.Workspace;
import sumcoda.webide.workspace.repository.WorkspaceRepository;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerminalService {

    private final EntryRepository entryRepository;

    private final WorkspaceRepository workspaceRepository;

    private final CompileService compileService;

    /**
     * 주어진 명령어를 실행하고 결과를 문자열로 반환
     *
     * @param workspaceId 워크스페이스 ID
     * @param command 실행할 명령어
     * @param session HTTP 세션
     * @return 명령어 실행 결과
     **/
    @Transactional
    public String executeCommand(Long workspaceId, String command, HttpSession session) throws IOException, InterruptedException {
        String currentPath = getCurrentPath(session, workspaceId);
        log.info("currentPath" + currentPath);

        String[] parts = command.split("\\s+");
        String cmd = parts[0];

        switch (cmd) {
            case "ls":
                return ls(workspaceId, currentPath);
            case "cd":
                if (parts.length > 1) {
                    return cd(workspaceId, parts[1], session);
                } else {
                    return "Invalid command: cd requires a path";
                }
            case "cat":
                if (parts.length > 1) {
                    return cat(workspaceId, parts[1], currentPath);
                } else {
                    return "Invalid command: cat requires a file path";
                }
            case "touch":
                if (parts.length > 1) {
                    return touch(workspaceId, parts[1], currentPath);
                } else {
                    return "Invalid command: touch requires a file name";
                }
            default:
                return handleCompileAndRunCommand(workspaceId, command, currentPath);
        }
    }

    /**
     * 현재 디렉토리의 파일 및 디렉토리 목록 반환
     * @param workspaceId 워크스페이스 ID
     * @param currentPath 현재 경로
     * @return 파일 및 디렉토리 목록
     */
    private String ls(Long workspaceId, String currentPath) {
        Entry currentEntry = getEntryByPath(workspaceId, currentPath).orElse(null);
        if (currentEntry == null || !currentEntry.getIsDirectory()) {
            return "Current path not found or not a directory";
        }

        List<EntryResponseDTO> children = entryRepository.findChildrenDTO(currentEntry.getId());
        return children.stream().map(EntryResponseDTO::getName).collect(Collectors.joining("\n"));
    }

    /**
     * 주어진 경로로 이동
     * @param workspaceId 워크스페이스 ID
     * @param targetPath 이동할 경로
     * @param session HTTP 세션
     * @return 이동 결과 메시지
     */
    private String cd(Long workspaceId, String targetPath, HttpSession session) {
        String currentPath = getCurrentPath(session, workspaceId);

        if (targetPath.equals("..")) {
            String newPath = resolvePath(currentPath, targetPath);
            setCurrentPath(session, workspaceId, newPath);
            return "Changed directory to " + newPath;
        }

        Entry targetEntry = getEntryByPath(workspaceId, resolvePath(currentPath, targetPath)).orElse(null);
        if (targetEntry == null || !targetEntry.getIsDirectory()) {
            return "Target path not found or not a directory";
        }

        String newPath = resolvePath(currentPath, targetPath);
        setCurrentPath(session, workspaceId, newPath);
        return "Changed directory to " + newPath;
    }

    /**
     * 파일의 내용을 출력
     * @param workspaceId 워크스페이스 ID
     * @param filePath 파일 경로
     * @param currentPath 현재 경로
     * @return 파일 내용
     */
    private String cat(Long workspaceId, String filePath, String currentPath) {
        String resolvedPath = resolvePath(currentPath, filePath);
        Entry fileEntry = getEntryByPath(workspaceId, resolvedPath).orElse(null);
        if (fileEntry == null || fileEntry.getIsDirectory()) {
            return "File not found or is a directory";
        }

        return fileEntry.getContent();
    }

    /**
     * 새로운 파일을 생성
     *
     * @param workspaceId 워크스페이스 ID
     * @param newFileName 생성할 파일 이름
     * @param currentPath 현재 경로
     * @return 파일 생성 결과 메시지
     */
    public String touch(Long workspaceId, String newFileName, String currentPath) {

        log.info("currentPath : " + currentPath);
        Entry currentEntry = getEntryByPath(workspaceId, currentPath).orElse(null);

        if (currentEntry == null || !currentEntry.getIsDirectory()) {
            return "Path not found or not a directory";
        }

        // 현재 경로에 동일한 이름의 파일이나 디렉토리가 있는지 확인
        EntryResponseDTO existingEntry = entryRepository.findByWorkspaceIdAndParentIdAndNameDTO(workspaceId, currentEntry.getId(), newFileName).orElse(null);
        if (existingEntry == null) {
            return "File or directory with the same name already exists";
        }

        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);


        boolean isDirectory = true;
        if (newFileName.contains(".")) {
            isDirectory = false;
        }
        Entry newEntry = Entry.createEntry(newFileName, "", isDirectory, currentEntry, workspace);

        entryRepository.save(newEntry);

        return "File created: " + newFileName;
    }

    /**
     * 컴파일 및 실행 명령어 처리
     *
     * @param workspaceId 워크스페이스 ID
     * @param command 명령어
     * @param currentPath 현재 경로
     * @return 처리 결과
     */
    private String handleCompileAndRunCommand(Long workspaceId, String command, String currentPath) throws IOException, InterruptedException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            return "Unknown command.";
        }

        String fileName = parts[1];
        String extension = getFileExtension(fileName);

        if (!isValidCompileCommand(parts[0], extension)) {
            return "Unknown command.";
        }

        String resolvedPath = resolvePath(currentPath, fileName);
        Entry fileEntry = getEntryByPath(workspaceId, resolvedPath).orElse(null);
        if (fileEntry == null || fileEntry.getIsDirectory()) {
            return "File does not exist or is a directory.";
        }

        String code = fileEntry.getContent();
        String result = compileService.compileAndExecute(extension, code);

        return getCompileSuccessMessage(extension, fileName) + "\n" + result;
    }

    /**
     * 파일 확장자를 가져옴
     *
     * @param fileName 파일 이름
     * @return 파일 확장자
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }

    /**
     * 주어진 명령어와 파일 확장자가 유효한지 확인
     *
     * @param command 명령어
     * @param extension 파일 확장자
     * @return 유효 여부
     */
    private boolean isValidCompileCommand(String command, String extension) {
        return switch (command) {
            case "gcc" -> ".c".equals(extension);

            case "g++" -> ".cpp".equals(extension);

            case "javac" -> ".java".equals(extension);

            case "python" -> ".py".equals(extension);

            case "node" -> ".js".equals(extension);

            default -> false;
        };
    }

    /**
     * 컴파일 성공 메시지를 반환
     *
     * @param extension 파일 확장자
     * @return 성공 메시지
     */
    private String getCompileSuccessMessage(String extension, String fileName) {
        return switch (extension) {
            case ".c", ".cpp", ".java" -> fileName + " successfully compiled and executed.";

            case ".py", ".js" -> fileName + " successfully executed.";

            default -> "Unknown file type.";
        };
    }

    /**
     * 경로를 기준으로 엔트리 조회
     * @param workspaceId 워크스페이스 ID
     * @param path 조회할 경로
     * @return 엔트리 객체
     */
    private Optional<Entry> getEntryByPath(Long workspaceId, String path) {
        log.info("path: " + path);

        if (path.equals("/")) {
            return entryRepository.findRootByWorkspaceId(workspaceId);
        }

        return entryRepository.findByPath(workspaceId, path);
    }

    /**
     * 주어진 경로를 기반으로 절대 경로 생성
     * @param currentPath 현재 경로
     * @param targetPath 이동할 경로
     * @return 절대 경로
     */
    private String resolvePath(String currentPath, String targetPath) {
        if (targetPath.equals("..")) {
            int lastSlashIndex = currentPath.lastIndexOf('/');
            if (lastSlashIndex > 0) {
                return currentPath.substring(0, lastSlashIndex);
            } else {
                return "/";
            }
        } else {
            if (currentPath.equals("/")) {
                return "/" + targetPath;
            } else {
                return currentPath + "/" + targetPath;
            }
        }
    }

    /**
     * 세션에서 현재 경로를 가져옴
     * @param session HTTP 세션
     * @param workspaceId 워크스페이스 ID
     * @return 현재 경로
     */
    private String getCurrentPath(HttpSession session, Long workspaceId) {
        String sessionKey = "currentPath_" + workspaceId;
        String path = (String) session.getAttribute(sessionKey);
        if (path == null) {
            path = getWorkspaceRootPath(workspaceId);
            session.setAttribute(sessionKey, path);
        }
        return path;
    }

    /**
     * 워크스페이스의 루트 경로를 가져옴
     * @param workspaceId 워크스페이스 ID
     * @return 루트 경로
     */
    private String getWorkspaceRootPath(Long workspaceId) {
        // 루트 엔트리를 조회하여 경로 반환
        Optional<EntryResponseDTO> rootEntry = entryRepository.findRootByWorkspaceIdDTO(workspaceId);
        return rootEntry.map(entry -> "/" + entry.getName()).orElse("/");
    }

    /**
     * 세션에 현재 경로를 설정
     * @param session HTTP 세션
     * @param workspaceId 워크스페이스 ID
     * @param path 현재 경로
     */
    private void setCurrentPath(HttpSession session, Long workspaceId, String path) {
        String sessionKey = "currentPath_" + workspaceId;
        session.setAttribute(sessionKey, path);
    }

    // 파일 편집 모드 시작
//    private String startEditing(Long workspaceId, String filePath, HttpSession session) {
//        Entry fileEntry = getEntryByPath(workspaceId, resolvePath(currentPath, filePath)).orElse(null);
//        if (fileEntry == null || fileEntry.getIsDirectory()) {
//            return "File not found or is a directory";
//        }
//
//        session.setAttribute("editingFile", fileEntry);
//        session.setAttribute("fileContentBuffer", new StringBuilder());
//        return "Editing file: " + fileEntry.getName();
//    }

    // 파일 편집 중 처리
//    private String handleFileEditing(String input, HttpSession session) {
//        if (input.equals(":wq")) {
//            Entry fileEntry = (Entry) session.getAttribute("editingFile");
//            StringBuilder buffer = (StringBuilder) session.getAttribute("fileContentBuffer");
//            fileEntry.assignContent(buffer.toString());
//            entryRepository.save(fileEntry);
//            session.removeAttribute("editingFile");
//            session.removeAttribute("fileContentBuffer");
//            return "File saved: " + fileEntry.getName();
//        } else {
//            StringBuilder buffer = (StringBuilder) session.getAttribute("fileContentBuffer");
//            buffer.append(input).append("\n");
//            return "";
//        }
//    }

    // 각 워크스페이스별로 독립적으로 현재 경로를 관리할수 없다.
    // TerminalService가 필드로 currentPath 변수를 가지고 있기 떄문
//    /**
//     * 현재 경로를 반환
//     * @return 현재 경로
//     */
//    public String getCurrentPath() {
//        return this.currentPath;
//    }




}



