package sumcoda.webide.workspace.dto;

import lombok.*;
import sumcoda.webide.memberworkspace.enumerate.MemberWorkspaceRole;

@Getter @Setter
@NoArgsConstructor
public class WorkspaceAccessDTO {

    private boolean isPublic;

    private MemberWorkspaceRole role;

    @Builder
    public WorkspaceAccessDTO(MemberWorkspaceRole role, boolean isPublic) {
        this.isPublic = isPublic;
        this.role = role;
    }
}
