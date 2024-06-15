package sumcoda.webide.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sumcoda.webide.member.dto.MemberResponseDTO;

import java.util.Optional;

import static sumcoda.webide.member.domain.QMember.*;
import static sumcoda.webide.member.domain.QProfileImage.*;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Optional<MemberResponseDTO> findOneByUsername(String username) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.fields(MemberResponseDTO.class,
                        member.username,
                        member.password,
                        member.nickname,
                        member.email,
                        member.role,
                        profileImage.awsS3SavedFileURL
                ))
                .from(member)
                .leftJoin(member.profileImage, profileImage)
                .where(member.username.eq(username))
                .fetchOne());
    }
}
