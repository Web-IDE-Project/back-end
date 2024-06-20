package sumcoda.webide.workspace.dto;

import lombok.*;
import sumcoda.webide.memberworkspace.enumerate.MemberWorkspaceRole;

@Getter @Setter
@NoArgsConstructor
public class WorkspacePublicStatusDTO {

    private boolean isPublic;


    @Builder
    public WorkspacePublicStatusDTO(MemberWorkspaceRole role, boolean isPublic) {
        this.isPublic = isPublic;
    }
}
