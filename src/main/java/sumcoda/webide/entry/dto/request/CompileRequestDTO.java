package sumcoda.webide.entry.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompileRequestDTO {

    private String extension;

    private String code;

    @Builder
    public CompileRequestDTO(String extension, String code) {
        this.extension = extension;
        this.code = code;
    }
}
