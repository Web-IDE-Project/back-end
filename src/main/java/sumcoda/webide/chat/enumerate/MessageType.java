package sumcoda.webide.chat.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    ENTER("ENTER"),
    EXIT("EXIT"),
    TALK("TALK");

    private final String value;
}
