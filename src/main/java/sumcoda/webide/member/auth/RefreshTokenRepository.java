package sumcoda.webide.member.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsByToken(String token);

    void deleteByToken(String token);

}
