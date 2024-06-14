package sumcoda.webide.entry.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryCreateRequestDTO {
    private String name;
    private Boolean isDirectory;

    @Builder
    public EntryCreateRequestDTO(String name, Boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }
}
