package com.fesi6.team1.study_group.domain.meetup.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateMeetupRequestDTO {

    private String title;
    private MeetingType meetingType;
    private MeetupLocation location;
    private String content;
    private LocalDateTime recruitmentEndDate;
    private LocalDateTime meetingStartDate;
    private LocalDateTime meetingEndDate;
    private int maxParticipants;
    private int minParticipants;
    private Boolean isOnline;
}

