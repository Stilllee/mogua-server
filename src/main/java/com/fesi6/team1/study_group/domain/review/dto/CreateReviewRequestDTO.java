package com.fesi6.team1.study_group.domain.review.dto;

import com.fesi6.team1.study_group.domain.review.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateReviewRequestDTO {

    private Long meetupId;
    private int rating;
    private String content;

    public CreateReviewRequestDTO() {
    }
}
