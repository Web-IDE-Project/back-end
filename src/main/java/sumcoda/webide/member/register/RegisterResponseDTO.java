package sumcoda.webide.member.register;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterResponseDTO {

    // 아이디
    private String username;

    private String nickname;

    @Builder
    public RegisterResponseDTO(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }
}
