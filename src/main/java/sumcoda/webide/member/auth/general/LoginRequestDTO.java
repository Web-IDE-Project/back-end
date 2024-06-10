package sumcoda.webide.member.auth.general;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDTO {

    String username;

    String password;

    @Builder
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
