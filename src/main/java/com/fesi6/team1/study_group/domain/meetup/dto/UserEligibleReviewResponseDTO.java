package com.fesi6.team1.study_group.domain.meetup.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserEligibleReviewResponseDTO {
    private Long meetupId;
    private MeetupStatus status;
    private Boolean isOnline;
    private MeetupLocation location;
    private String title;
    private int maxParticipants;
    private int minParticipants;
    private LocalDateTime recruitmentStartDate;
    private LocalDateTime recruitmentEndDate;
    private LocalDateTime meetingStartDate;
    private LocalDateTime meetingEndDate;
    private String thumbnail;
    private int participantsCount;

    public UserEligibleReviewResponseDTO(Meetup meetup, int count) {
        meetupId = meetup.getId();
        status = meetup.getStatus();
        isOnline = meetup.getIsOnline();
        location = meetup.getLocation();
        title = meetup.getTitle();
        maxParticipants = meetup.getMaxParticipants();
        minParticipants = meetup.getMinParticipants();
        recruitmentStartDate = meetup.getMeetingStartDate();
        recruitmentEndDate = meetup.getRecruitmentEndDate();
        meetingStartDate = meetup.getMeetingStartDate();
        meetingEndDate = meetup.getMeetingEndDate();
        thumbnail = meetup.getThumbnail();
        participantsCount = count;
    }
}
