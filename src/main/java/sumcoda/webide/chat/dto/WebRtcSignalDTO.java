package sumcoda.webide.chat.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WebRtcSignalDTO {
  private String peerId;
  private String sdp;
  private String candidate;
}
