package com.fesi6.team1.study_group.domain.user.dto;

import com.fesi6.team1.study_group.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String profileImg;
    private String jwtToken;

    public UserLoginResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getNickname();
        this.profileImg = user.getProfileImg();
    }
}

