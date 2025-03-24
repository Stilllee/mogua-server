package com.fesi6.team1.study_group.domain.user.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserMeetupResponseDTO {

    private final String title;
    private final MeetupLocation location;
    private final int currentParticipants;
    private final LocalDateTime recruitmentStartDate;
    private final LocalDateTime recruitmentEndDate;
    private final LocalDateTime meetingStartDate;
    private final LocalDateTime meetingEndDate;
    private final String thumbnail;
    private final boolean isFavorite;
    private final boolean isOwnId;


    public UserMeetupResponseDTO(Meetup meetup, boolean isFavorite, Long userId, Long profileUserId) {
        this.title = meetup.getTitle();
        this.location = meetup.getIsOnline() ? null : meetup.getLocation();
        this.currentParticipants = meetup.getMeetupUsers().size(); // 현재 참가자 수
        this.recruitmentStartDate = meetup.getCreatedAt();
        this.recruitmentEndDate = meetup.getRecruitmentEndDate();
        this.meetingStartDate = meetup.getMeetingStartDate();
        this.meetingEndDate = meetup.getMeetingEndDate();
        this.thumbnail = meetup.getThumbnail();
        this.isFavorite = isFavorite;
        this.isOwnId = (userId.equals(profileUserId));
    }

}