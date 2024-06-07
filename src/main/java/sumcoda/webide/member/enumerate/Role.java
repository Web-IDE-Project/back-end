package sumcoda.webide.member.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("USER"),
    ADMIN("ADMIN");

    private final String value;
}