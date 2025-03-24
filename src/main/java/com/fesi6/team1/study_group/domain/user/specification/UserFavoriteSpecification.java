package com.fesi6.team1.study_group.domain.user.specification;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.user.entity.UserFavorite;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserFavoriteSpecification {

    // User ID filter
    public static Specification<UserFavorite> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId != null) {
                return criteriaBuilder.equal(root.get("user").get("id"), userId);
            }
            return null;
        };
    }

    // Type filter
    public static Specification<UserFavorite> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type != null && !"ALL".equals(type)) {
                try {
                    MeetingType meetingType = MeetingType.valueOf(type.toUpperCase());
                    return criteriaBuilder.equal(root.get("meetup").get("type"), meetingType);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        };
    }

    // Location filter
    public static Specification<UserFavorite> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if (location != null && !location.equals("ALL")) {
                return criteriaBuilder.equal(root.get("location"), location);
            }
            return null;
        };
    }

    public static Specification<UserFavorite> hasValidRecruitmentPeriod() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime now = LocalDateTime.now();
            return criteriaBuilder.greaterThan(root.get("meetup").get("recruitmentEndDate"), now);
        };
    }

}