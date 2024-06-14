package sumcoda.webide.entry.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryRenameRequestDTO {
    private String name;
    private Boolean isDirectory;

    @Builder
    public EntryRenameRequestDTO(String name, Boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }
}
