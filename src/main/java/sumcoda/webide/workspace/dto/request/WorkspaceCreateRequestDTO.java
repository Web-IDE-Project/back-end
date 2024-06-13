package sumcoda.webide.workspace.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sumcoda.webide.workspace.enumerate.Language;

@Getter @Setter
@NoArgsConstructor
public class WorkspaceCreateRequestDTO {

    private String title;
    private String description;
    private Language language;

    @Builder
    public WorkspaceCreateRequestDTO(String title, String description, Language language) {
        this.title = title;
        this.description = description;
        this.language = language;
    }
}
