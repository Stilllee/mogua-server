package com.fesi6.team1.study_group.domain.meetup.repository;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MeetupUserRepository extends JpaRepository<MeetupUser, Long> {
    // 특정 모임에 참가한 사용자를 찾는 메서드
    Optional<MeetupUser> findByMeetupIdAndUserId(Long meetupId, Long userId);

    // 이미 참가한 모임에서 해당 사용자를 삭제하는 메서드
    void delete(MeetupUser meetupUser);

    @Query("SELECT mu.meetup FROM MeetupUser mu WHERE mu.user.id = :userId AND mu.meetup.meetingType = :type")
    Page<Meetup> findByUserIdAndType(@Param("userId") Long userId, @Param("type") MeetingType type, Pageable pageable);

    int countByMeetupId(Long meetupId);

    @Query("SELECT mu FROM MeetupUser mu WHERE mu.user.id = :userId AND mu.meetup.meetingType = :meetingType AND mu.hasReview = false")
    Page<MeetupUser> findByUserIdAndMeetup_MeetingTypeAndHasReviewFalse(Long userId, MeetingType meetingType, Pageable pageable);

    @Query("SELECT mu FROM MeetupUser mu WHERE mu.user.id = :userId AND mu.meetup.meetingType = :meetingType AND mu.hasReview = true")
    Page<Meetup> findByUserIdAndMeetingTypeAndHasReviewTrue(Long userId, MeetingType meetingType, Pageable pageable);

    @Query("SELECT mu FROM MeetupUser mu " +
            "JOIN mu.meetup m " +
            "WHERE mu.user.id = :userId " +
            "AND mu.hasReview = false " +
            "AND m.meetingType = :type " +
            "AND m.meetingEndDate <= CURRENT_TIMESTAMP " +
            "AND m.meetingEndDate >= :fiveDaysAgo")
    Page<MeetupUser> findEligibleReviews(@Param("userId") Long userId,
                                         @Param("type") MeetingType type,
                                         @Param("fiveDaysAgo") LocalDateTime fiveDaysAgo,
                                         Pageable pageable);


}
