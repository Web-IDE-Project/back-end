package sumcoda.webide.entry.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntryGetResponseDTO {

    private String content;

    @Builder
    public EntryGetResponseDTO(String content) {
        this.content = content;
    }
}
