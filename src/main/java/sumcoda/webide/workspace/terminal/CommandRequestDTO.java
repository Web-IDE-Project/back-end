package sumcoda.webide.workspace.terminal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommandRequestDTO {
    private String command;

    @Builder
    public CommandRequestDTO(String command) {
        this.command = command;
    }
}
