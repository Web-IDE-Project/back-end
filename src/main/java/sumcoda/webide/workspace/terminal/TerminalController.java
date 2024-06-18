package sumcoda.webide.workspace.terminal;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TerminalController {

    private final TerminalService terminalService;

    /**
     * 주어진 명령어를 실행하고 결과를 반환
     *
     * @param workspaceId 워크스페이스 ID
     * @param commandRequestDTO 실행할 명령어
     * @param session HTTP 세션
     * @return 명령어 실행 결과
     */
    @PostMapping("/api/workspaces/{workspaceId}/terminal/execute")
    public ResponseEntity<?> executeCommand(@PathVariable Long workspaceId, @RequestBody CommandRequestDTO commandRequestDTO, HttpSession session) throws IOException, InterruptedException {
        Map<String, Object> responseData = new HashMap<>();

        String result = terminalService.executeCommand(workspaceId, commandRequestDTO.getCommand(), session);
        responseData.put("result", result);
        
        return ResponseEntity.ok(responseData);
    }
}
