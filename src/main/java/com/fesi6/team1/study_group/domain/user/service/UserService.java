package com.fesi6.team1.study_group.domain.user.service;

import com.fesi6.team1.study_group.domain.meetup.dto.MeetupListResponseDTO;
import com.fesi6.team1.study_group.domain.meetup.dto.MeetupResponseDTO;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupService;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupUserService;
import com.fesi6.team1.study_group.domain.review.entity.Review;
import com.fesi6.team1.study_group.domain.review.service.ReviewService;
import com.fesi6.team1.study_group.domain.user.dto.*;
import com.fesi6.team1.study_group.domain.user.entity.LoginType;
import com.fesi6.team1.study_group.domain.user.entity.User;
import com.fesi6.team1.study_group.domain.user.entity.UserTag;
import com.fesi6.team1.study_group.domain.user.repository.UserRepository;
import com.fesi6.team1.study_group.global.common.s3.S3FileService;
import com.fesi6.team1.study_group.global.security.jwt.JwtCookieUtil;
import com.fesi6.team1.study_group.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MeetupUserService meetupUserService;
    private final JwtCookieUtil jwtCookieUtil;
    private final S3FileService s3FileService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ResponseCookie createAccessTokenCookie(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        return jwtCookieUtil.createAccessTokenCookie(accessToken);
    }

    public ResponseCookie createRefreshTokenCookie(Long userId) {
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User 찾을 수 없음"));

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return jwtCookieUtil.createRefreshTokenCookie(refreshToken);
    }

    public ResponseCookie reissueAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("잘못된 refresh token");
        }

        Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User 찾을 수 없음"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("잘못된 Refresh token");
        }
        return createAccessTokenCookie(userId);
    }

    public User kakaoSave(KakaoUserInfoDTO kakaoUserInfoDto) {
        return userRepository.findBySocialId(String.valueOf(kakaoUserInfoDto.getSocialId()))
                .orElseGet(() -> {
                    User newUser = User.socialUserBuilder()
                            .socialId(String.valueOf(kakaoUserInfoDto.getSocialId()))
                            .email(kakaoUserInfoDto.getEmail())
                            .nickname(kakaoUserInfoDto.getNickname())
                            .loginType(LoginType.SOCIAL)
                            .profileImg(kakaoUserInfoDto.getProfileImage())
                            .build();
                    return userRepository.save(newUser);
                });
    }

    public Map<String, Object> customSave(UserSignRequestDTO request) throws IOException {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.customUserBuilder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .loginType(LoginType.CUSTOM)
                .build();
        user.setPassword(encodedPassword);

        String basePath = "https://mogua.s3.ap-northeast-2.amazonaws.com/profileImage/";
        int randomNum = new Random().nextInt(4) + 1;
        String fileName = basePath + "defaultProfileImages/" + randomNum + ".png";
        user.setProfileImg(fileName);

        userRepository.save(user);

        Map<String, Object> userData = Map.of(
                "email", user.getEmail(),
                "name", user.getNickname()
        );

        return Map.of(
                "user", userData,
                "userId", user.getId()
        );
    }


    public UserLoginResponseDTO customLogin(UserLoginRequestDTO request) throws IOException {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록된 이메일이 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String jwtToken = jwtTokenProvider.createAccessToken(user.getId());
        UserLoginResponseDTO userResponse = new UserLoginResponseDTO(user);
        userResponse.setJwtToken(jwtToken);
        return userResponse;
    }

    public void updatePassword(Long userId, UpdatePasswordRequestDTO request) {

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    public UserProfileResponseDTO findUserProfile(Long userId, Long myId) {
        return new UserProfileResponseDTO(findById(userId),myId);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }

    public void updateMyProfile(Long userId, MultipartFile file, UpdateProfileRequestDTO request) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Optional.ofNullable(request.getNickname())
                .ifPresent(user::setNickname);

        Optional.ofNullable(request.getBio())
                .ifPresent(user::setBio);

        Optional.ofNullable(request.getUserTagList())
                .ifPresent(tags -> {
                    user.getTags().clear();
                    List<UserTag> newTags = tags.stream()
                            .map(tag -> {
                                UserTag userTag = new UserTag();
                                userTag.setUser(user);
                                userTag.setTag(tag);
                                return userTag;
                            })
                            .collect(Collectors.toList());
                    user.getTags().addAll(newTags);
                });

        if (file != null) {
            updateProfileImage(user, file);
        }
        userRepository.save(user);
    }

    private void updateProfileImage(User user, MultipartFile file) throws IOException {

        String path = "profileImage";
        String currentProfileImg = user.getProfileImg();
        String basePath = "https://mogua.s3.ap-northeast-2.amazonaws.com/profileImage/";
        boolean isDefaultImage = currentProfileImg == null || currentProfileImg.startsWith(basePath + "defaultProfileImages/");

        if (!isDefaultImage && currentProfileImg != null) {
            String oldFilePath = currentProfileImg.replace(basePath, "");
            s3FileService.deleteFile(oldFilePath);
        }

        String uploadedFileName = s3FileService.uploadFile(file, path);
        user.setProfileImg(basePath + uploadedFileName);
    }

    /***
     *
     * 유저 모임 조회
     *
     ***/
    // 수정버전
    public MeetupListResponseDTO getUserMeetupsByType(Long profileUserId, MeetingType meetingType, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.unsorted());
        Page<Meetup> meetupPage = meetupUserService.findByUserIdAndType(profileUserId, meetingType, pageable);

        List<MeetupResponseDTO> meetupResponseDTOList = meetupPage.getContent().stream()
                .sorted((meetup1, meetup2) -> {
                    int statusComparison = compareMeetupStatus(meetup1.getStatus(), meetup2.getStatus());
                    if (statusComparison != 0) {
                        return statusComparison;
                    }
                    return meetup1.getMeetingStartDate().compareTo(meetup2.getMeetingStartDate());
                })
                .map(meetup -> new MeetupResponseDTO(meetup))
                .collect(Collectors.toList());

        Integer nextPage = meetupPage.hasNext() ? page + 1 : -1;
        boolean isLast = meetupPage.isLast();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("nextPage", nextPage);
        additionalData.put("isLast", isLast);

        return new MeetupListResponseDTO(meetupResponseDTOList, additionalData);
    }

    private int compareMeetupStatus(MeetupStatus status1, MeetupStatus status2) {
        int[] statusOrder = { 0, 1, 2, 3 };  // RECRUITING, BEFORE_START, IN_PROGRESS, COMPLETED 순서
        return Integer.compare(statusOrder[status1.ordinal()], statusOrder[status2.ordinal()]);
    }

}