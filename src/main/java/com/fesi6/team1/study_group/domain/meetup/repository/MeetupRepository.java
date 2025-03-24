package com.fesi6.team1.study_group.domain.meetup.repository;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    List<Meetup> findByMeetingType(MeetingType meetingType);

    Page<Meetup> findAll(Specification<Meetup> spec, Pageable pageable);

    Page<Meetup> findByHostIdAndMeetingType(Long hostId, MeetingType meetingType, Pageable pageable);

}
