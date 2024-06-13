package sumcoda.webide.entry.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryCreateRequestDTO {
    private String name;

    @Builder
    public EntryCreateRequestDTO(String name) {
        this.name = name;
    }
}
