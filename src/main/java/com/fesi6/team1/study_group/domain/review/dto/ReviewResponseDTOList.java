package com.fesi6.team1.study_group.domain.review.dto;

import com.fesi6.team1.study_group.domain.meetup.dto.MeetupResponseDTO;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ReviewResponseDTOList {
    private List<ReviewResponseDTO> review;
    private Map<String, Object> additionalData;

    public ReviewResponseDTOList(List<ReviewResponseDTO> review, Map<String, Object> additionalData) {
        this.review = review;
        this.additionalData = additionalData;
    }
}

