package sumcoda.webide.member.auth.general;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthResponseDTO {

    private String username;

    private String nickname;

//    private String imageURL;

    @Builder
    public AuthResponseDTO(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }
}
