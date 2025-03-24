package com.fesi6.team1.study_group.domain.review.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class UserWrittenReviewResponseDTOList {
    private List<UserWrittenReviewResponseDTO> writtenReview;
    private Map<String, Object> additionalData;

    public UserWrittenReviewResponseDTOList(List<UserWrittenReviewResponseDTO> review, Map<String, Object> additionalData) {
        this.writtenReview = review;
        this.additionalData = additionalData;
    }
}
