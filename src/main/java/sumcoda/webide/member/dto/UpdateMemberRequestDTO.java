package sumcoda.webide.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateMemberRequestDTO {

    private String nickname;

    private String password;

    @Builder
    public UpdateMemberRequestDTO(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }
}