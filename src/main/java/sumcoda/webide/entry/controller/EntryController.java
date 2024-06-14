package sumcoda.webide.entry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.entry.dto.request.EntryCreateRequestDTO;
import sumcoda.webide.entry.dto.request.EntryRenameRequestDTO;
import sumcoda.webide.entry.dto.response.EntryCreateResponseDTO;
import sumcoda.webide.entry.service.EntryService;
import sumcoda.webide.member.auth.social.CustomOAuth2User;
import sumcoda.webide.workspace.dto.response.WorkspaceEntriesResponseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;

    // 엔트리 생성
    @PostMapping("/{workspaceId}/entries/{parentId}")
    public ResponseEntity<List<WorkspaceEntriesResponseDTO>> createEntry(
            @PathVariable Long workspaceId,
            @PathVariable Long parentId,
            @RequestBody EntryCreateRequestDTO entryCreateRequestDTO,
            Authentication authentication) {

        String username = "";
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2.0 사용자
            CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
            username = oauthUser.getUsername();

            // 그외 사용자
        } else {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        }

        List<WorkspaceEntriesResponseDTO> response = entryService.createEntry(workspaceId, parentId, entryCreateRequestDTO, username);

        return ResponseEntity.ok(response);
    }

    // 디렉토리 삭제
    @DeleteMapping("/{containerId}/directories/{directoryId}")
    public ResponseEntity<Map<String, String>> deleteDirectory(
            @PathVariable Long containerId,
            @PathVariable Long directoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        entryService.deleteDirectory(containerId, directoryId, username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "디렉토리 삭제에 성공하였습니다.");

        return ResponseEntity.ok(response);
    }

    // 디렉토리 이름 수정
    @PutMapping("/{containerId}/directories/{directoryId}/rename")
    public ResponseEntity<Map<String, String>> renameDirectory(
            @PathVariable Long containerId,
            @PathVariable Long directoryId,
            @RequestBody EntryRenameRequestDTO entryRenameRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        entryService.renameDirectory(containerId, directoryId, entryRenameRequestDTO, username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "디렉토리 이름이 수정되었습니다.");

        return ResponseEntity.ok(response);
    }
}
