package sumcoda.webide.member.auth.general;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDTO {

    private String username;

    @Builder
    public LoginResponseDTO(String username) {
        this.username = username;
    }
}
