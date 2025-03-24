package com.fesi6.team1.study_group.domain.user.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class UserTutoringReviewResponseDTOList {
    private List<UserTutoringReviewResponseDTO> userTutoringReview;
    private Map<String, Object> additionalData;

    public UserTutoringReviewResponseDTOList(List<UserTutoringReviewResponseDTO> userTutoringReview, Map<String, Object> additionalData) {
        this.userTutoringReview = userTutoringReview;
        this.additionalData = additionalData;
    }
}
