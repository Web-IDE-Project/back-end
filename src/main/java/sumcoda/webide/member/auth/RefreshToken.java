package sumcoda.webide.member.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String token;

    // 토큰 만료일
    @Column(nullable = false)
    private String expiration;

    @Builder
    public RefreshToken(String username, String token, String expiration) {
        this.username = username;
        this.token = token;
        this.expiration = expiration;
    }
}
