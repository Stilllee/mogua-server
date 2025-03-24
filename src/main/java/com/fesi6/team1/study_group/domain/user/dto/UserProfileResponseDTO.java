package com.fesi6.team1.study_group.domain.user.dto;

import com.fesi6.team1.study_group.domain.user.entity.QualificationStatus;
import com.fesi6.team1.study_group.domain.user.entity.User;
import com.fesi6.team1.study_group.domain.user.entity.UserTag;
import lombok.Getter;

import java.util.List;

@Getter
public class UserProfileResponseDTO {

    private final String email;
    private final String nickname;
    private final String profileImg;
    private final QualificationStatus qualificationStatus;
    private final String bio;
    private final List<UserTag> userTagList;
    private final boolean isOwnId;

    public UserProfileResponseDTO(User user,Long myId) {

        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImg = user.getProfileImg();
        this.qualificationStatus = user.getQualificationStatus();
        this.bio = user.getBio();
        this.userTagList = user.getTags();
        this.isOwnId = (user.getId().equals(myId));
    }
}
