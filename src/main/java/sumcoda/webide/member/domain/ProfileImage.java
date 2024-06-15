package sumcoda.webide.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원본 파일명
    @Column(nullable = false)
    private String originalFilename;

    // UUID 를 활용하여 구성된 파일 이름
    @Column(nullable = false)
    private String savedFilename;

    // 이미지에 대한 URL 정보를 DB에서 찾을때 활용
    @Column(nullable = false)
    private String awsS3SavedFileURL;

    // 양방향 연관관계
    @OneToOne(mappedBy = "profileImage")
    private Member member;

    @Builder
    public ProfileImage(String originalFilename, String savedFilename, String awsS3SavedFileURL) {
        this.originalFilename = originalFilename;
        this.savedFilename = savedFilename;
        this.awsS3SavedFileURL = awsS3SavedFileURL;
    }

    // 직접 빌더 패턴의 생성자를 활용하지 말고 해당 메서드를 활용하여 엔티티 생성
    public static ProfileImage createProfileImage(String originalFilename, String savedFilename, String awsS3SavedFileURL) {
        return ProfileImage.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .awsS3SavedFileURL(awsS3SavedFileURL)
                .build();
    }

    // ProfileImage 1 <-> 1 Member
    // 양방향 연관관계 편의 메서드드
    public void assignMember(Member member) {
        if (this.member != null) {
            this.member.assignProfileImage(null);
        }
        this.member = member;
        if (member != null && member.getProfileImage() != this) {
            member.assignProfileImage(this);
        }
    }
}


