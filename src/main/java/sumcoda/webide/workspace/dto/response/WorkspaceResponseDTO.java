package sumcoda.webide.workspace.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sumcoda.webide.workspace.enumerate.Language;

@Slf4j

@Getter
public class WorkspaceResponseDTO {

    private Long id;
    private String title;
    private String language;
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL) // children 필드가 null 일 때 JSON 반환하지 않도록하는 어노테이션
    private String nickname;

//    @JsonInclude(JsonInclude.Include.NON_NULL) // children 필드가 null 일 때 JSON 반환하지 않도록하는 어노테이션
//    private String imageUrl;

    @Builder
    public WorkspaceResponseDTO(Long id, String title, String language, String description, String nickname) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.description = description;
        this.nickname = nickname;
        log.info(nickname);
//        this.imageUrl = imageUrl;
    }
}
