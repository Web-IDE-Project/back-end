package sumcoda.webide.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.entry.domain.Entry;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor
public class WorkspaceEntriesResponseDTO {

    private Long id;
    private String name;
    private Metadata metadata;

    @JsonInclude(JsonInclude.Include.NON_NULL) // children 필드가 null 일 때 JSON 반환하지 않도록하는 어노테이션
    private List<WorkspaceEntriesResponseDTO> children;

    @Getter
    @Builder
    public static class Metadata {
        private Boolean isDirectory;
        private String content;

    }

    @Builder
    public WorkspaceEntriesResponseDTO(Long id, String name, Boolean isDirectory, String content, List<WorkspaceEntriesResponseDTO> children) {
        this.id = id;
        this.name = name;
        this.metadata = Metadata.builder()
                .isDirectory(isDirectory)
                .content(content)
                .build();
        this.children = children;
    }

    public static WorkspaceEntriesResponseDTO fromEntity(Entry entry, Set<Long> setIds) {
        // 엔트리의 id가 이미 setIds애 포함되어 있으면 null 반환
        if (setIds.contains(entry.getId())) {
            return null;
        }
        // 현재 엔트리 id를 set에 추가
        setIds.add(entry.getId());

        List<WorkspaceEntriesResponseDTO> children =
                !entry.getChildren().isEmpty() ? entry.getChildren()
                        .stream()
                        .map(child -> fromEntity(child, setIds)) // 재귀적으로 하위 항목을 처리
                        .filter(Objects::nonNull) // null이 아닌 항목만 필터링
                        .toList() : null;

        return WorkspaceEntriesResponseDTO.builder()
                .id(entry.getId())
                .name(entry.getName())
                .isDirectory(entry.getIsDirectory())
                .content(entry.getContent())
                .children(children != null ? children.stream()
                        .filter(Objects::nonNull) // null이 아닌 항목만 필터링
                        .toList() : null
                )
                .build();
    }
}
