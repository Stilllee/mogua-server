package com.fesi6.team1.study_group.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupUser;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String socialId;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String nickname;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetupUser> meetupUsers;

    @Enumerated(EnumType.STRING)
    private QualificationStatus qualificationStatus;

    @Column(length = 20)  // 소개글
    private String bio = "";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserTag> tags = new ArrayList<>();

    private String refreshToken;

    @Builder(builderMethodName = "socialUserBuilder")
    public User(String socialId, String nickname, String email, String profileImg, LoginType loginType) {
        this.socialId = socialId;
        this.nickname = nickname;
        this.email = email;
        this.profileImg = profileImg;
        this.loginType = loginType;
        this.qualificationStatus = QualificationStatus.UNQUALIFIED;
    }

    @Builder(builderMethodName = "customUserBuilder")
    public User(String email, String password, String nickname, LoginType loginType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
