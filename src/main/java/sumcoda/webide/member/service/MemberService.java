package sumcoda.webide.member.service;

import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sumcoda.webide.common.config.AWSS3Config;
import sumcoda.webide.member.domain.Member;
import sumcoda.webide.member.domain.ProfileImage;
import sumcoda.webide.member.dto.FileDTO;
import sumcoda.webide.member.dto.UpdateMemberRequestDTO;
import sumcoda.webide.member.repository.MemberRepository;
import sumcoda.webide.member.util.FileStorageUtil;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    // 멤버를 조회하기 위한 필드
    private final MemberRepository memberRepository;

    //변경된 비밀번호를 암호화 하기 위한 필드
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // AWS S3 활용을 위해 필요한 설정 클래스
    private final AWSS3Config awss3Config;

    // S3에 등록된 버킷 이름
    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Transactional
    public void updateMemberInfos(String username, UpdateMemberRequestDTO updateMemberRequestDTO, MultipartFile profileImage) {
        // 사용자 이름으로 멤버 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 가진 사용자가 존재하지 않습니다. : " + username));

        // 닉네임이 null이 아니면 닉네임 업데이트
        if (updateMemberRequestDTO.getNickname() != null) {
            member.assignNickname(updateMemberRequestDTO.getNickname());
        }

        // 비밀번호가 null이 아니면 비밀번호 업데이트
        if (updateMemberRequestDTO.getPassword() != null && !updateMemberRequestDTO.getPassword().isEmpty()) {
            String encryptedPassword = bCryptPasswordEncoder.encode(updateMemberRequestDTO.getPassword());
            member.assignPassword(encryptedPassword);
        }

        if (profileImage == null || profileImage.isEmpty()) {
            member.assignProfileImage(null);
        } else {
            try {
                if (profileImage != null && !profileImage.isEmpty()) {


//                    // 이미지 로컬 저장시 필요한것
//                    FileDTO fileDTO = FileStorageUtil.saveFile(profileImage);
//                    String originalFilename = fileDTO.getOriginalFilename();
//                    String savedFilename = fileDTO.getSavedFilename();
//                    String fullLocalPath = FileStorageUtil.getLocalStoreDir(savedFilename);
//                    member.assignProfileImage(
//                            ProfileImage.createProfileImage(
//                                    originalFilename,
//                                    savedFilename,
//                                    fullLocalPath));

                    // 이미지 S3 저장시 필요한것
                    FileDTO fileDTO = FileStorageUtil.saveFile(profileImage);

                    String originalFilename = fileDTO.getOriginalFilename();
                    String savedFilename = fileDTO.getSavedFilename();
                    File file = fileDTO.getFile();

                    // S3에 이미지 저장
                    awss3Config.amazonS3Client().putObject(new PutObjectRequest(bucketName, savedFilename, file));
                    // 클라이언트가 해당 이미지를 요청할 수 있는 URL
                    String awsS3URL = awss3Config.amazonS3Client().getUrl(bucketName, savedFilename).toString();

                    member.assignProfileImage(
                            ProfileImage.createProfileImage(
                                    originalFilename,
                                    savedFilename,
                                    awsS3URL));
                    file.delete();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
