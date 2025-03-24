package com.fesi6.team1.study_group.domain.meetup.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantDTO {
    private Long userId;
    private String profileImageUrl;

    public ParticipantDTO(Long userId, String profileImageUrl) {
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
    }
}