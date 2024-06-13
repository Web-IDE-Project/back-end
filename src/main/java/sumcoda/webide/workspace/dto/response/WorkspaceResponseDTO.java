package sumcoda.webide.workspace.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.entry.domain.Entry;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class WorkspaceResponseDTO {
    private Long id;
    private String title;
    private Boolean isDirectory;
    private Long parentId;

    @Builder
    public WorkspaceResponseDTO(Long id, String title, Boolean isDirectory, Long parentId) {
        this.id = id;
        this.title = title;
        this.isDirectory = isDirectory;
        this.parentId = parentId;
    }

    // 단일 Entry 객체를 WorkspaceResponseDTO로 변환하는 메서드
    public static WorkspaceResponseDTO fromEntry(Entry entry) {
        return new WorkspaceResponseDTO(
                entry.getId(),
                entry.getName(),
                entry.getIsDirectory(),
                entry.getParent() != null ? entry.getParent().getId() : null
        );
    }

    // Entry 객체 목록을 WorkspaceResponseDTO 목록으로 변환하는 메서드
    public static List<WorkspaceResponseDTO> fromEntries(List<Entry> entries) {
        return entries.stream()
                .map(WorkspaceResponseDTO::fromEntry)
                .collect(Collectors.toList());
    }
}
