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

    // 소문자를 대문자로 바꾸고 ENUM 으로 변환하는 메서드
    public static Status parseStatus(String value) {
        return Status.valueOf(value.toUpperCase());
    }
}
