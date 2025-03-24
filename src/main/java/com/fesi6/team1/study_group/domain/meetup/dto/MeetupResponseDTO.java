package com.fesi6.team1.study_group.domain.meetup.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MeetupResponseDTO {

    private final Long meetupId;
    private final String title;
    private final MeetingType meetingType;
    private final MeetupLocation location;
    private final String content;
    private final LocalDateTime recruitmentStartDate;
    private final LocalDateTime recruitmentEndDate;
    private final LocalDateTime meetingStartDate;
    private final LocalDateTime meetingEndDate;
    private final int maxParticipants;
    private final int minParticipants;
    private final Boolean isOnline;
    private final String thumbnail;
    private final String hostNickname;
    private final Long hostId;
    private final List<ParticipantDTO> participants;
    private final MeetupStatus meetupStatus;

    public MeetupResponseDTO(Meetup meetup) {
        this.meetupId = meetup.getId();
        this.title = meetup.getTitle();
        this.meetingType = meetup.getMeetingType();

        if (meetup.getIsOnline()) {
            this.location = null;
        } else {
            this.location = meetup.getLocation();
        }
        this.content = meetup.getContent();
        this.recruitmentStartDate = meetup.getCreatedAt();
        this.recruitmentEndDate = meetup.getRecruitmentEndDate();
        this.meetingStartDate = meetup.getMeetingStartDate();
        this.meetingEndDate = meetup.getMeetingEndDate();
        this.maxParticipants = meetup.getMaxParticipants();
        this.minParticipants = meetup.getMinParticipants();
        this.isOnline = meetup.getIsOnline();
        this.thumbnail = meetup.getThumbnail();
        this.hostNickname = meetup.getHost().getNickname();
        this.hostId = meetup.getHost().getId();
        this.participants = meetup.getMeetupUsers().stream()
                .map(meetupUser -> new ParticipantDTO(
                        meetupUser.getUser().getId(),
                        meetupUser.getUser().getProfileImg()
                ))
                .collect(Collectors.toList());
        this.meetupStatus = meetup.getStatus();
    }
}

