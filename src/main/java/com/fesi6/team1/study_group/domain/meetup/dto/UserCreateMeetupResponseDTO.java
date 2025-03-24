package com.fesi6.team1.study_group.domain.meetup.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserCreateMeetupResponseDTO {
    private Long meetupId;
    private MeetupStatus meetupStatus;
    private Boolean isOnline;
    private MeetupLocation location;
    private String title;
    private LocalDateTime recruitmentStartDate;
    private LocalDateTime recruitmentEndDate;
    private LocalDateTime meetingStartDate;
    private LocalDateTime meetingEndDate;
    private String thumbnail;
    private int participants;

    public UserCreateMeetupResponseDTO(Meetup meetup) {
        meetupId = meetup.getId();
        meetupStatus = meetup.getStatus();
        isOnline = meetup.getIsOnline();
        location = meetup.getLocation();
        title = meetup.getTitle();
        recruitmentStartDate = meetup.getCreatedAt();
        recruitmentEndDate = meetup.getRecruitmentEndDate();
        meetingStartDate = meetup.getMeetingStartDate();
        meetingEndDate = meetup.getMeetingEndDate();
        thumbnail = meetup.getThumbnail();
        participants = meetup.getParticipantCount();
    }
}
