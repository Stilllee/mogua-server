package com.fesi6.team1.study_group.domain.user.dto;

import lombok.Getter;

@Getter
public class UserSignRequestDTO {
    private String email;
    private String password;
    private String nickname;
}