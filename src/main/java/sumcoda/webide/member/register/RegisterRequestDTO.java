package sumcoda.webide.member.register;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDTO {

    private String username;

    private String password;

    private String nickname;

    private String email;

    @Builder
    public RegisterRequestDTO(String username, String password, String nickname, String email) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
}
