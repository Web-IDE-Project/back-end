package sumcoda.webide.entry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.entry.dto.request.EntryCreateRequestDTO;
import sumcoda.webide.entry.dto.request.EntryRenameRequestDTO;
import sumcoda.webide.entry.dto.request.EntrySaveRequestDTO;
import sumcoda.webide.entry.dto.response.EntryCreateResponseDTO;
import sumcoda.webide.entry.dto.response.EntryGetResponseDTO;
import sumcoda.webide.entry.service.EntryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;

    // 파일 생성
    @PostMapping("/{containerId}/files/{directoryId}")
    public ResponseEntity<EntryCreateResponseDTO> createFile(
            @PathVariable Long containerId,
            @PathVariable Long directoryId,
            @RequestBody EntryCreateRequestDTO entryCreateRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        EntryCreateResponseDTO response = entryService.createFile(containerId, directoryId, entryCreateRequestDTO, username);
        return ResponseEntity.ok(response);
    }

    // 파일 저장
    @PutMapping("/{containerId}/files/{fileId}")
    public ResponseEntity<Map<String, String>> saveFile(
            @PathVariable Long containerId,
            @PathVariable Long fileId,
            @RequestBody EntrySaveRequestDTO entrySaveRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        entryService.saveFile(containerId, fileId, entrySaveRequestDTO, username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "파일 저장에 성공하였습니다.");

        return ResponseEntity.ok(response);
    }
}
