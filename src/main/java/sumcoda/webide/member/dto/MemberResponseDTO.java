package sumcoda.webide.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.member.enumerate.Role;

@NoArgsConstructor
@Getter
public class MemberResponseDTO {

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Role role;

    private String awsS3SavedFileURL;


    @Builder
    public MemberResponseDTO(String username, String password, String nickname, String email, Role role, String awsS3SavedFileURL) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.awsS3SavedFileURL = awsS3SavedFileURL;
    }
}
