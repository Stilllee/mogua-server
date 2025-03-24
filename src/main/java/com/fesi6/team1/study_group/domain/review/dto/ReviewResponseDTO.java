package com.fesi6.team1.study_group.domain.review.dto;

import com.fesi6.team1.study_group.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;
    private Long meetupId;
    private String userNickname;
    private String content;
    private int rating;
    private String thumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponseDTO(Review review) {
        reviewId = review.getId();
        meetupId = review.getMeetup().getId();
        userNickname = review.getUser().getNickname();
        content = review.getContent();
        rating = review.getRating();
        thumbnail = review.getThumbnail();
        createdAt = review.getCreatedAt();
        updatedAt = review.getUpdatedAt();
    }
}
