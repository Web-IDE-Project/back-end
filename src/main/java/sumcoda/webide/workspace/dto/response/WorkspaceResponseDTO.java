package sumcoda.webide.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.entry.domain.Entry;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspaceResponseDTO {

    private Long id;
    private String name;
    private Boolean isDirectory;
    private String content;
    private List<WorkspaceResponseDTO> children;

    @Builder
    public WorkspaceResponseDTO(Long id, String name, Boolean isDirectory, String content, List<WorkspaceResponseDTO> children) {
        this.id = id;
        this.name = name;
        this.isDirectory = isDirectory;
        this.content = content;
        this.children = children;
    }

    // 단일 Entry 객체를 WorkspaceResponseDTO로 변환하는 메서드
    public static WorkspaceResponseDTO fromEntry(Entry entry) {
        // 하위 엔트리가 비어있으면 null로 설정, 그렇지 않으면 하위 엔트리를 리스트로 설정
        List<WorkspaceResponseDTO> children = entry.getChildren().isEmpty() ? null : entry.getChildren().stream()
                .map(WorkspaceResponseDTO::fromEntry) // 각 하위 엔트리를 재귀적으로 변환
                .collect(Collectors.toList());

        // 빌더를 사용하여 WorkspaceResponseDTO 객체를 생성하여 반환
        return WorkspaceResponseDTO.builder()
                .id(entry.getId())
                .name(entry.getName())
                .isDirectory(entry.getIsDirectory())
                .content(entry.getContent())
                .children(children)
                .build();
    }

    // Entry 객체 목록을 WorkspaceResponseDTO 목록으로 변환하는 메서드
    public static List<WorkspaceResponseDTO> fromEntries(List<Entry> entries) {
        // 트리 구조 반환을 위해 부모가 없는 최상위 엔트리만 필터링하여 변환
        return entries.stream()
                .filter(entry -> entry.getParent() == null) // 부모가 없는 엔트리만 선택
                .map(WorkspaceResponseDTO::fromEntry) //각 엔트리를 WorkspaceResponseDTO로 변환
                .collect(Collectors.toList());
    }
}
