package sumcoda.webide.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.workspace.dto.request.WorkspaceCreateRequestDTO;
import sumcoda.webide.workspace.service.WorkspaceService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    // 워크스페이스 생성 로직 메서드 호출을 위한 필드
    private final WorkspaceService workspaceService;

    /**
     * 워크스페이스 생성 요청 캐치
     *
     * @param workspaceCreateRequestDTO 프론트로부터 전달받은 워크스페이스 정보
     * @param authentication            인증 정보
     */
    //워크스페이스 생성 컨트롤러
    @PostMapping
    public ResponseEntity<?> createWorkspace(
            @RequestBody WorkspaceCreateRequestDTO workspaceCreateRequestDTO,
            Authentication authentication) {

        String username;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        username = userDetails.getUsername();

        Map<String, Object> responseData = new HashMap<>();
        try {
            workspaceService.createWorkspace(workspaceCreateRequestDTO, username);
            responseData.put("message", "워크스페이스가 생성되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("result", "error");
            responseData.put("message", "워크스페이스가 정상적으로 생성되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }
}
