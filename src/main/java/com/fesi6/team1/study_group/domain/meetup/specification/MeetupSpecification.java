package com.fesi6.team1.study_group.domain.meetup.specification;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupLocation;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupStatus;
import org.springframework.data.jpa.domain.Specification;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalTime;

public class MeetupSpecification {

    public static Specification<Meetup> hasType(MeetingType type) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("meetingType"), type);
    }

    public static Specification<Meetup> hasState(MeetupStatus state) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), state);
    }

    public static Specification<Meetup> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if ("ALL".equals(location)) {
                return criteriaBuilder.conjunction();
            }

            try {
                MeetupLocation meetupLocation = MeetupLocation.valueOf(location);
                return criteriaBuilder.equal(root.get("location"), meetupLocation);
            } catch (IllegalArgumentException e) {
                throw new InvalidParameterException("Invalid location value: " + location);
            }
        };
    }

    public static Specification<Meetup> startDateAfter(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("meetingStartDate"), date.atStartOfDay());
    }

    public static Specification<Meetup> endDateBefore(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("meetingEndDate"), date.atTime(LocalTime.MAX));
    }
}


