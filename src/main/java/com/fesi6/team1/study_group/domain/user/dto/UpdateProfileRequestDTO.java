package com.fesi6.team1.study_group.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequestDTO {

    private String nickname;
    private String bio;
    private List<String> userTagList;

    public UpdateProfileRequestDTO(String nickname, String bio, List<String> userTagList) {
        this.nickname = nickname;
        this.bio = bio;
        this.userTagList = userTagList;
    }
}