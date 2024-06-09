package sumcoda.webide.workspace.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    MY("My"),
    QUESTION("Question"),
    LECTURE("Lecture");

    private final String value;
}
