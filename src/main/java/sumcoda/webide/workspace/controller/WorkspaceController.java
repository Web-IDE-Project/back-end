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
import sumcoda.webide.workspace.dto.request.WorkspaceUpdateRequestDTO;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;
import sumcoda.webide.workspace.enumerate.Category;
import sumcoda.webide.workspace.enumerate.Status;
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

        Long response = workspaceService.createWorkspace(workspaceCreateRequestDTO, username);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", response);

        return ResponseEntity.ok(responseData);
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

        //워크스페이스 실행 서비스 호출
        WorkspaceEntriesResponseDTO workspaceEntries = workspaceService.getAllEntriesByWorkspaceId(workspaceId, username);

        return ResponseEntity.ok(workspaceEntries);
    }

    @GetMapping("/{category}/get")
    public ResponseEntity<?> getWorkspacesByCategory(@PathVariable Category category, Authentication authentication) {

        Map<String, Object> responseData = new HashMap<>();
        log.info(category.getValue());

        String username;

        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();
            log.info("유저이름 : " + username);


            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
            log.info("유저이름 : " + username);
        }

        try {
            List<?> workspacesByCategory = workspaceService.getWorkspacesByCategory(category, username);
            return ResponseEntity.status(HttpStatus.OK).body(workspacesByCategory);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.put("message", category.getValue() + " 워크스페이스를 조회할 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseData);
        }

    }

    // 워크 스페이스 수정
    @PutMapping("/{workspaceId}")
    public ResponseEntity<?> updateWorkspace(
            @PathVariable Long workspaceId,
            @RequestBody WorkspaceUpdateRequestDTO workspaceUpdateRequestDTO,
            Authentication authentication) {

        String username = "";

        if (authentication instanceof OAuth2AuthenticationToken) {
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        workspaceService.updateWorkspace(workspaceId, workspaceUpdateRequestDTO, username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "워크스페이스 정보가 수정되었습니다.");

        return ResponseEntity.ok(response);
    }

    // 워크스페이스 상태 수정
    @PutMapping("/{workspaceId}/{status}")
    public ResponseEntity<?> updateWorkspaceStatus(
            @PathVariable Long workspaceId,
            @PathVariable String status,
            Authentication authentication) {

        String username = "";

        if (authentication instanceof OAuth2AuthenticationToken) {
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        workspaceService.updateWorkspaceStatus(workspaceId, Status.parseStatus(status), username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "워크스페이스 상태 정보가 수정되었습니다.");

        return ResponseEntity.ok(response);
    }
}
