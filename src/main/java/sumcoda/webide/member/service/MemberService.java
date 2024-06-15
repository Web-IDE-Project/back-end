package sumcoda.webide.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
        if (updateMemberRequestDTO.getPassword() != null) {
            member.assignPassword(updateMemberRequestDTO.getPassword());
        }

        if (profileImage == null || profileImage.isEmpty()) {
            member.assignProfileImage(null);
        } else {
            try {
                if (profileImage != null && !profileImage.isEmpty()) {
                    FileDTO fileDTO = FileStorageUtil.saveFile(profileImage);
                    String originalFilename = fileDTO.getOriginalFilename();
                    String savedFilename = fileDTO.getSavedFilename();
                    String fullLocalPath = FileStorageUtil.getLocalStoreDir(savedFilename);
//                    File file = fileDTO.getFile();
//
                    member.assignProfileImage(
                            ProfileImage.createProfileImage(
                                    originalFilename,
                                    savedFilename,
                                    fullLocalPath));
//                file.delete();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
