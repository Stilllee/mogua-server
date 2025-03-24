package com.fesi6.team1.study_group.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDTO {

    private Long socialId;
    private String nickname;
    private String email;
    private String profileImage;

    public KakaoUserInfoDTO(Long socialId, String email, String nickname, String profileImage) {
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
