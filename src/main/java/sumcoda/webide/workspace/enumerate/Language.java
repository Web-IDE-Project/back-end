package sumcoda.webide.workspace.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {

    C("C"),
    CPP("C++"),
    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("Javascript");

    private final String value;
}
