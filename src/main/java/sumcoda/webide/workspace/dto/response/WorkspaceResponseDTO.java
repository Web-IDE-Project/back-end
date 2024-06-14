package sumcoda.webide.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WorkspaceResponseDTO {

    private Long id;
    private String name;
    private Boolean isDirectory;
    private String content;

    @JsonInclude(JsonInclude.Include.NON_NULL) // children 필드가 null 일 때 JSON 반환하지 않도록하는 어노테이션
    private List<WorkspaceResponseDTO> children;

    @Builder
    public WorkspaceResponseDTO(Long id, String name, Boolean isDirectory, String content, List<WorkspaceResponseDTO> children) {
        this.id = id;
        this.name = name;
        this.isDirectory = isDirectory;
        this.content = content;
        this.children = children;
    }
}
