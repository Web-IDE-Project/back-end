package sumcoda.webide.entry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sumcoda.webide.entry.dto.request.EntryCreateRequestDTO;
import sumcoda.webide.entry.dto.request.EntryRenameRequestDTO;
import sumcoda.webide.entry.dto.response.EntryCreateResponseDTO;
import sumcoda.webide.entry.service.EntryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;

    // 디렉토리 생성
    @PostMapping("/{containerId}/directories/{directoryId}")
    public ResponseEntity<EntryCreateResponseDTO> createDirectory(
            @PathVariable Long containerId,
            @PathVariable Long directoryId,
            @RequestBody EntryCreateRequestDTO entryCreateRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        EntryCreateResponseDTO response = entryService.createDirectory(containerId, directoryId, entryCreateRequestDTO, username);

        return ResponseEntity.ok(response);
    }
}
