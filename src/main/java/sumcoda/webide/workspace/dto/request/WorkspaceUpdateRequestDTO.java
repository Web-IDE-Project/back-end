package sumcoda.webide.workspace.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.workspace.enumerate.Category;

@Getter
@NoArgsConstructor
public class WorkspaceUpdateRequestDTO {
    private String title;
    private String description;
    private Category category;

    @Builder
    public WorkspaceUpdateRequestDTO(String title, String description, Category category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }
}