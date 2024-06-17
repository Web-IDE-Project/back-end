package sumcoda.webide.entry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryResponseDTO {

    private Long id;

    private String name;

    private Boolean isDirectory;

    private String content;

    private Long parentId;

    @Builder
    public EntryResponseDTO(Long id, String name, Boolean isDirectory, String content, Long parentId) {
        this.id = id;
        this.name = name;
        this.isDirectory = isDirectory;
        this.content = content;
        this.parentId = parentId;
    }
}
