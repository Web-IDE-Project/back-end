package sumcoda.webide.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.member.auth.social.CustomOAuth2User;
import sumcoda.webide.workspace.dto.request.WorkspaceCreateRequestDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceResponseDTO;
import sumcoda.webide.workspace.exception.WorkspaceNotFoundException;
import sumcoda.webide.workspace.service.WorkspaceService;

import java.util.HashMap;
import java.util.List;
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

        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }
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

    /**
     * 워크스페이스 실행 시 엔트리 목록 반환
     *
     * @param workspaceId    워크스페이스 ID
     * @param authentication 인증 정보
     */
    @PostMapping("/{workspaceId}")
    public ResponseEntity<?> executeWorkspace(
            @PathVariable Long workspaceId,
            Authentication authentication) {

        Map<String, Object> responseData = new HashMap<>();
        try {
            List<WorkspaceResponseDTO> workspaceEntries = workspaceService.executeWorkspace(workspaceId);
            return ResponseEntity.status(HttpStatus.OK).body(workspaceEntries);
        } catch (WorkspaceNotFoundException e) {
            responseData.put("result", "error");
            responseData.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
        }
    }
}
