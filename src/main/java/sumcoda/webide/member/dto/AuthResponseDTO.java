package sumcoda.webide.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthResponseDTO {

    private String username;

    private String nickname;

    private String awsS3SavedFileURL;

    @Builder
    public AuthResponseDTO(String username, String nickname, String awsS3SavedFileURL) {
        this.username = username;
        this.nickname = nickname;
        this.awsS3SavedFileURL = awsS3SavedFileURL;
    }
}
