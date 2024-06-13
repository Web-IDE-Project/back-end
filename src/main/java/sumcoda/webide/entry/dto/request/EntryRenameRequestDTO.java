package sumcoda.webide.entry.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryRenameRequestDTO {
    private String name;

    @Builder
    public EntryRenameRequestDTO(String name) {
        this.name = name;
    }
}
