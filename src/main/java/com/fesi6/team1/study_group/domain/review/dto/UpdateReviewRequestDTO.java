package com.fesi6.team1.study_group.domain.review.dto;

import com.fesi6.team1.study_group.domain.review.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateReviewRequestDTO {

    private int rating;
    private String content;
    private LocalDateTime reviewUpdateDate;

    public UpdateReviewRequestDTO() {
    }

}
