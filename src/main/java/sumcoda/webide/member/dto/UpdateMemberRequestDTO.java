package sumcoda.webide.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateMemberRequestDTO {

    private String nickname;

    private String password;
}