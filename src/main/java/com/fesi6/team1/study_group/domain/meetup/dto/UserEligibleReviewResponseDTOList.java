package com.fesi6.team1.study_group.domain.meetup.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class UserEligibleReviewResponseDTOList {
    private List<UserEligibleReviewResponseDTO> eligibleReview;
    private Map<String, Object> additionalData;

    public UserEligibleReviewResponseDTOList(List<UserEligibleReviewResponseDTO> review, Map<String, Object> additionalData) {
        this.eligibleReview = review;
        this.additionalData = additionalData;
    }
}
