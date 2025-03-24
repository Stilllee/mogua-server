package com.fesi6.team1.study_group.domain.meetup.service;

import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupRequest;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupUser;
import com.fesi6.team1.study_group.domain.meetup.repository.MeetupRequestRepository;
import com.fesi6.team1.study_group.domain.user.entity.User;
import com.fesi6.team1.study_group.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MeetupRequestService {

    private final MeetupRequestRepository meetupRequestRepository;
    private final MeetupService meetupService;
    private final MeetupUserService meetupUserService;
    private final UserService userService;

    public void requestMeetup(Long meetupId, Long userId) {
        // 1. 유저 및 모임 확인
        Meetup meetup = meetupService.findById(meetupId);
        User user = userService.findById(userId);

        if (meetup.getRecruitmentEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("모집 기간이 종료되었습니다");
        }

        boolean alreadyRequested = meetupUserService.findByMeetupIdAndUserId(meetupId, userId).isPresent();
        if (alreadyRequested) {
            throw new IllegalStateException("이미 이 모임에 신청하셨습니다");
        }

        // 2. 신청 정보 생성
        MeetupRequest request = MeetupRequest.builder()
                .meetup(meetup)
                .user(user)
                .build();

        // 3. 신청 정보 저장
        meetupRequestRepository.save(request);

        MeetupUser meetupUser = MeetupUser.builder()
                .meetup(meetup)
                .user(user)
                .hasReview(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        meetupUserService.save(meetupUser);

        meetup.setParticipantCount(meetup.getParticipantCount() + 1);
        meetupService.save(meetup);
    }

    public void leaveMeetup(Long meetupId, Long userId) {
        meetupUserService.leaveMeetup(meetupId, userId);
        Meetup meetup = meetupService.findById(meetupId);
        meetup.setParticipantCount(meetup.getParticipantCount() + 1);
        meetupService.save(meetup);
    }

}