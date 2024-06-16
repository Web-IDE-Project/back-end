package sumcoda.webide.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ValidatePasswordRequestDTO {

    private String password;

    @Builder
    public ValidatePasswordRequestDTO(String password) {
        this.password = password;
    }
}
