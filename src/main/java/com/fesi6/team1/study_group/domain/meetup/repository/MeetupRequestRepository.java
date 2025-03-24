package com.fesi6.team1.study_group.domain.meetup.repository;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetupRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetupRequestRepository extends JpaRepository<MeetupRequest, Long> {

    boolean existsByMeetupIdAndUserId(Long meetupId, Long userId);
}
