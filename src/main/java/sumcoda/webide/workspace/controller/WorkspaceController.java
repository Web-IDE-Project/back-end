package sumcoda.webide.workspace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.member.auth.social.CustomOAuth2User;
import sumcoda.webide.workspace.dto.request.WorkspaceCreateRequestDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.enumerate.Category;
import sumcoda.webide.workspace.exception.WorkspaceFoundException;
import sumcoda.webide.workspace.service.WorkspaceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
            Long workspaceId = workspaceService.createWorkspace(workspaceCreateRequestDTO, username);
            workspaceService.createWorkspace(workspaceCreateRequestDTO, username);
            responseData.put("id", workspaceId);
        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("result", "error");
            responseData.put("message", "워크스페이스가 정상적으로 생성되지 않았습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    /**
     * 워크스페이스 실행 시 엔트리 목록 반환하는 메서드
     *
     * @param workspaceId    워크스페이스 ID
     * @param authentication 인증 정보
     */
    @GetMapping("/{workspaceId}")
    public ResponseEntity<?> executeWorkspace(
            @PathVariable Long workspaceId,
            Authentication authentication) {

        Map<String, Object> responseData = new HashMap<>();
        try {
            //워크스페이스 실행 서비스 호출
            WorkspaceEntriesResponseDTO workspaceEntries = workspaceService.getAllEntriesByWorkspaceId(workspaceId);
            return ResponseEntity.status(HttpStatus.OK).body(workspaceEntries);
        } catch (WorkspaceFoundException e) {
            responseData.put("result", "error");
            responseData.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
        }
    }

    @GetMapping("/{category}/get")
    public ResponseEntity<?> getWorkspacesByCategory(@PathVariable Category category) {

        Map<String, Object> responseData = new HashMap<>();
        log.info(category.getValue());

        try {
            List<?> workspacesByCategory = workspaceService.getWorkspacesByCategory(category);
            return ResponseEntity.status(HttpStatus.OK).body(workspacesByCategory);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", category.getValue() + " 워크스페이스를 조회할 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

    }
}
