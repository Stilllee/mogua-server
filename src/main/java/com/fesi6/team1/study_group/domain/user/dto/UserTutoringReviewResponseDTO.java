package com.fesi6.team1.study_group.domain.user.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import com.fesi6.team1.study_group.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserTutoringReviewResponseDTO {
    private Long userId;
    private String nickname;
    private String profileImg;
    private int rating;
    private Long meetupId;
    private String title;
    private String content;
    private LocalDateTime meetingEndDate;
    private String reviewThumbnail;
    private LocalDateTime reviewDate;

    public UserTutoringReviewResponseDTO(Review review) {

        userId = review.getUser().getId();
        nickname = review.getUser().getNickname();
        profileImg = review.getUser().getProfileImg();
        rating = review.getRating();
        meetupId = review.getMeetup().getId();
        title = review.getMeetup().getTitle();
        content = review.getContent();
        meetingEndDate = review.getMeetup().getMeetingEndDate();
        reviewThumbnail = review.getThumbnail();
        reviewDate = review.getCreatedAt();
    }
}