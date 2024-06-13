package sumcoda.webide.entry.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntrySaveRequestDTO {
    private String content;

    @Builder
    public EntrySaveRequestDTO(String content) {
        this.content = content;
    }
}
