package sumcoda.webide.member.auth.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // token을 전달받아 내부에 메서드를 통해서 username 확인
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 해당 토큰이 암호화가 되어있으니까 가지고있는 토큰이 우리 서버에서 생성되었는지, 우리가 가지고 있는 키가 맞는지에대해 검증을 진행한다
                .build()
                .parseSignedClaims(token) // Claims를 확인하고
                .getPayload() // payload 부분에서 특정한 데이터를 가지고온다.
                .get("username", String.class); // username을 String 형태로 가져온다.
    }

    // token을 전달받아 내부에 메서드를 통해서 role 확인
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 해당 토큰이 암호화가 되어있으니까 가지고있는 토큰이 우리 서버에서 생성되었는지, 우리가 가지고 있는 키가 맞는지에대해 검증을 진행한다
                .build()
                .parseSignedClaims(token) // caims를 확인하고
                .getPayload()// payload 부분에서 특정한 데이터를 가지고온다.
                .get("role", String.class); // role을 String 형태로 가져온다.
    }

    // token을 전달받아 내부에 메서드를 통해서 access / refresh를 확인하기위한 category 확인
    public String getCategory(String token) {

        return Jwts.parser().
                verifyWith(secretKey) // 해당 토큰이 암호화가 되어있으니까 가지고있는 토큰이 우리 서버에서 생성되었는지, 우리가 가지고 있는 키가 맞는지에대해 검증을 진행한다
                .build()
                .parseSignedClaims(token)
                .getPayload()// payload 부분에서 특정한 데이터를 가지고온다.
                .get("category", String.class); // category를 String 형태로 가져온다.
    }

    // token을 전달받아 내부에 JWT pares().verifyWith() 메서드를 통해서 해당 토큰이 만료되었는지 확인
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 해당 토큰이 암호화가 되어있으니까 가지고있는 토큰이 우리 서버에서 생성되었는지, 우리가 가지고 있는 키가 맞는지에대해 검증을 진행한다
                .build()
                .parseSignedClaims(token) // claims를 확인
                .getPayload()// payload 부분에서 특정한 데이터를 가지고온다.
                .getExpiration()// 만료되었는지 여부를 가져온다.
                .before(new Date(System.currentTimeMillis())); // 특정한 날짜 전에
    }           // System.currentTimeMillis() + 시간으로 하기


    // 로그인이 성공하면 커스텀한 로그인 필터에서 success 핸들러를 통해서 전달받은 인자값을 바탕으로 토큰 발급
    public String createJwt(String category, String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
