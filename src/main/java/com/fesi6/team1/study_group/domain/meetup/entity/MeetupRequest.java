package com.fesi6.team1.study_group.domain.meetup.entity;

import com.fesi6.team1.study_group.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fesi6.team1.study_group.domain.meetup.entity.RequestStatus.PENDING;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MeetupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //신청 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetup_id", nullable = false)
    private Meetup meetup;  //해당 모임

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //신청한 사용자

//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private RequestStatus status = PENDING;  //상태(PENDING, APPROVED, REJECTED)

    @Column(nullable = false)
    private LocalDateTime requestedAt=LocalDateTime.now();;  //신청한 시간

    private LocalDateTime processedAt;  //처리된 시간

    @Builder
    public MeetupRequest(Meetup meetup, User user) {
        this.meetup = meetup;
        this.user = user;
    }

}
