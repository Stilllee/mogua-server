package com.fesi6.team1.study_group.domain.user.dto;

import com.fesi6.team1.study_group.domain.meetup.dto.ParticipantDTO;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class WishlistMeetupResponseDTO {
    private Long meetupId;
    private MeetingType meetingType;
    private Boolean isOnline;
    private MeetupStatus meetupStatus;
    private MeetupLocation location;
    private String title;
    private String content;
    private int minParticipants;
    private LocalDateTime recruitmentStartDate;
    private LocalDateTime recruitmentEndDate;
    private LocalDateTime meetingStartDate;
    private LocalDateTime meetingEndDate;
    private String thumbnail;
    private final List<ParticipantDTO> participants;

    public WishlistMeetupResponseDTO(Meetup meetup) {
        meetupId = meetup.getId();
        meetingType = meetup.getMeetingType();
        isOnline = meetup.getIsOnline();
        meetupStatus = meetup.getStatus();
        location = meetup.getLocation();
        title = meetup.getTitle();
        content = meetup.getContent();
        minParticipants = meetup.getMinParticipants();
        recruitmentStartDate = meetup.getCreatedAt();
        recruitmentEndDate = meetup.getRecruitmentEndDate();
        meetingStartDate = meetup.getMeetingStartDate();
        meetingEndDate = meetup.getMeetingEndDate();
        thumbnail = meetup.getThumbnail();
        this.participants = meetup.getMeetupUsers().stream()
                .map(meetupUser -> new ParticipantDTO(
                        meetupUser.getUser().getId(),
                        meetupUser.getUser().getProfileImg()
                ))
                .collect(Collectors.toList());
    }
}
