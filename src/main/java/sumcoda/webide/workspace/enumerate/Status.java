package sumcoda.webide.workspace.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    DEFAULT("Default"),
    SOLVE("Solve"),
    COMPLETE("Complete");

    private final String value;
}
