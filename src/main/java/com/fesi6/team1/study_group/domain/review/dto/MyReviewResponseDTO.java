package com.fesi6.team1.study_group.domain.review.dto;

import com.fesi6.team1.study_group.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyReviewResponseDTO {

    private final int rating;
    private final String title;
    private final String content;
    private final LocalDateTime meetingStartDate;
    private final LocalDateTime meetingEndDate;
    private final String userNickname;
    private final LocalDateTime reviewDate;

    public MyReviewResponseDTO(Review review) {
        this.title = review.getMeetup().getTitle();
        this.content = review.getContent();
        this.meetingStartDate = review.getMeetup().getMeetingStartDate();
        this.meetingEndDate = review.getMeetup().getMeetingEndDate();
        this.rating = review.getRating();
        this.reviewDate = review.getCreatedAt();
        this.userNickname = review.getUser().getNickname();
    }

}
