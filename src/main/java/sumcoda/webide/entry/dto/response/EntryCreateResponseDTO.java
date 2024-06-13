package sumcoda.webide.entry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryCreateResponseDTO {

    private Long id;
    private String name;
    private Boolean isDirectory;
    private Long parentId;

    @Builder
    public EntryCreateResponseDTO(Long id, String name, boolean isDirectory, Long parentId) {
        this.id = id;
        this.name = name;
        this.isDirectory = isDirectory;
        this.parentId = parentId;
    }
}
