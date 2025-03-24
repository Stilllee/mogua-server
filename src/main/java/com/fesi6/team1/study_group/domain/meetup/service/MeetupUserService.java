package com.fesi6.team1.study_group.domain.meetup.service;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupUser;
import com.fesi6.team1.study_group.domain.meetup.repository.MeetupUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetupUserService {

    private final MeetupUserRepository meetupUserRepository;

    public void save(MeetupUser meetupUser) {
        meetupUserRepository.save(meetupUser);
    }

    public Optional<MeetupUser> findByMeetupIdAndUserId(Long meetupId, Long userId) {
        return meetupUserRepository.findByMeetupIdAndUserId(meetupId, userId);
    }

    public void leaveMeetup(Long meetupId, Long userId) {
        MeetupUser meetupUser = meetupUserRepository.findByMeetupIdAndUserId(meetupId, userId)
                .orElseThrow(() -> new IllegalStateException("모임에 참가하지 않았습니다"));
        meetupUserRepository.delete(meetupUser);
    }

    public Page<Meetup> findByUserIdAndType(Long userId, MeetingType meetingType, Pageable pageable) {
        return meetupUserRepository.findByUserIdAndType(userId, meetingType, pageable);
    }

    public int getParticipantsCount(Long meetupId) {
        return meetupUserRepository.countByMeetupId(meetupId);
    }

    public Page<MeetupUser> findByUserIdAndTypeAndHasReviewFalse(Long userId, MeetingType type, Pageable pageable) {
        return meetupUserRepository.findByUserIdAndMeetup_MeetingTypeAndHasReviewFalse(userId, type, pageable);
    }

    public Page<MeetupUser> findEligibleReviews(Long userId, MeetingType type, Pageable pageable) {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
        return meetupUserRepository.findEligibleReviews(userId, type, fiveDaysAgo, pageable);
    }
}
