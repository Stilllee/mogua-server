package com.fesi6.team1.study_group.domain.user.service;

import com.fesi6.team1.study_group.domain.meetup.dto.UserCreateMeetupResponseDTO;
import com.fesi6.team1.study_group.domain.meetup.dto.UserCreateMeetupResponseDTOList;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupRequest;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupUser;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupService;
import com.fesi6.team1.study_group.domain.user.dto.WishlistMeetupResponseDTO;
import com.fesi6.team1.study_group.domain.user.dto.WishlistMeetupResponseDTOList;
import com.fesi6.team1.study_group.domain.user.entity.User;
import com.fesi6.team1.study_group.domain.user.entity.UserFavorite;
import com.fesi6.team1.study_group.domain.user.repository.UserFavoriteRepository;
import com.fesi6.team1.study_group.domain.user.specification.UserFavoriteSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UserFavoriteRepository userFavoriteRepository;
    private final UserService userService;
    private final MeetupService meetupService;

    public void requestWishlist(Long meetupId, Long userId) {
        // 1. 유효성 검사
        Meetup meetup = meetupService.findById(meetupId);
        User user = userService.findById(userId);

        if (meetup.getRecruitmentEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("모집 기간이 종료되었습니다");
        }

        if (userFavoriteRepository.findByMeetupIdAndUserId(meetupId, userId).isPresent()) {
            throw new IllegalStateException("이미 이 모임을 찜하셨습니다");
        }

        // 2. 찜 정보 생성
        UserFavorite userFavorite = UserFavorite.builder()
                .user(user)
                .meetup(meetup)
                .build();

        // 3. 찜 정보 저장
        userFavoriteRepository.save(userFavorite);
    }

    public void deleteWishlist(Long meetupId, Long userId) {
        // 1. 유효성 검사
        Meetup meetup = meetupService.findById(meetupId);
        User user = userService.findById(userId);

        if (meetup.getRecruitmentEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("모집 기간이 종료되었습니다");
        }

        if (!userFavoriteRepository.findByMeetupIdAndUserId(meetupId, userId).isPresent()) {
            throw new IllegalStateException("이미 이 모임을 찜하지 않았습니다.");
        }

        // 2. 찜 정보 삭제
        UserFavorite userFavorite = userFavoriteRepository.getByMeetupIdAndUserId(meetupId, userId);
        userFavoriteRepository.delete(userFavorite);
    }

    public WishlistMeetupResponseDTOList getUserWishlist(Long userId, int page, int limit, String orderBy, String type, String location) {
        Pageable pageable = PageRequest.of(page, limit, getSortBy(orderBy));
        Specification<UserFavorite> spec = Specification.where(UserFavoriteSpecification.hasUserId(userId))
                .and(UserFavoriteSpecification.hasValidRecruitmentPeriod());

        if (!"ALL".equals(type)) {
            spec = spec.and(UserFavoriteSpecification.hasType(type));
        }
        if (!"ALL".equals(location)) {
            spec = spec.and(UserFavoriteSpecification.hasLocation(location));
        }

        Page<UserFavorite> userFavorites = userFavoriteRepository.findAll(spec, pageable);

        List<WishlistMeetupResponseDTO> wishlistMeetupResponseDTOList = userFavorites.getContent().stream()
                .filter(userFavorite -> userFavorite.getMeetup() != null)
                .map(userFavorite -> new WishlistMeetupResponseDTO(userFavorite.getMeetup()))
                .collect(Collectors.toList());

        Integer nextPage = userFavorites.hasNext() ? page + 1 : -1;
        boolean isLast = userFavorites.isLast();

        Map<String, Object> additionalData = Map.of(
                "nextPage", nextPage,
                "isLast", isLast
        );

        return new WishlistMeetupResponseDTOList(wishlistMeetupResponseDTOList, additionalData);
    }

    private Sort getSortBy(String orderBy) {
        switch (orderBy) {
            case "deadline":
                return Sort.by(Sort.Direction.ASC, "recruitmentEndDate"); // 마감일 오름차순 (임박순)
            case "participant":
                return Sort.by(Sort.Direction.DESC, "participantCount"); // 참여 인원 내림차순
            case "latest":
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt"); // 최신순
        }
    }
}