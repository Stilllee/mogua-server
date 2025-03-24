package com.fesi6.team1.study_group.domain.review.repository;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByMeetupId(Long meetupId, Pageable pageable);

    @EntityGraph(attributePaths = {"meetup"})
    @Query("SELECT r FROM Review r JOIN Meetup m ON r.meetup.id = m.id WHERE r.user.id = :userId AND m.meetingType = :type")
    Page<Review> findByUserIdAndType(@Param("userId") Long userId, @Param("type") MeetingType type, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN Meetup m ON r.meetup.id = m.id WHERE r.user.id = :userId AND m.meetingType = :type")
    Page<Review> findByUserIdAndMeetingType(@Param("userId") Long userId, @Param("type") MeetingType type, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN Meetup m ON r.meetup.id = m.id WHERE m.host.id = :userId AND m.meetingType = :type")
    Page<Review> findByHostIdAndMeetingType(@Param("userId") Long userId, @Param("type") MeetingType type, Pageable pageable);

}
