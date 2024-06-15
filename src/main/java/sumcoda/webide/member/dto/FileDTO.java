package sumcoda.webide.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;

@Getter
@NoArgsConstructor
public class FileDTO {

    private String originalFilename;

    private String savedFilename;

    private File file;

    @Builder
    public FileDTO(String originalFilename, String savedFilename, File file) {
        this.originalFilename = originalFilename;
        this.savedFilename = savedFilename;
        this.file = file;
    }
}
