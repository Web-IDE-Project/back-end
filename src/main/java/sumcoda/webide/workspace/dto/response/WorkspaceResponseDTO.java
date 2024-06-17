package sumcoda.webide.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
public class WorkspaceResponseDTO {

    private Long id;

    private String title;

    private String language;

    private String description;

    private String status;

    // nickname 필드가 null 일 때 JSON 반환하지 않도록하는 어노테이션
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickname;

    private String awsS3SavedFileURL;


    @Builder
    public WorkspaceResponseDTO(Long id, String title, String language, String description, String status, String nickname, String awsS3SavedFileURL) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.description = description;
        this.status = status;
        this.nickname = nickname;
        this.awsS3SavedFileURL = awsS3SavedFileURL;
    }

    @Getter
    @NoArgsConstructor
    public static class My {

        private Long id;

        private String title;

        private String language;

        private String description;

        private String status;

        // nickname 필드가 null 일 때 JSON 반환하지 않도록하는 어노테이션
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String nickname;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String awsS3SavedFileURL;



        @Builder
        public My(Long id, String title, String language, String description, String status, String nickname, String awsS3SavedFileURL) {
            this.id = id;
            this.title = title;
            this.language = language;
            this.description = description;
            this.status = status;
            this.nickname = nickname;
            this.awsS3SavedFileURL = awsS3SavedFileURL;
        }
    }


}
