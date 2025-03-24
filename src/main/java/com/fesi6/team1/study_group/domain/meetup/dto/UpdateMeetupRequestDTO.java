package com.fesi6.team1.study_group.domain.meetup.dto;

import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMeetupRequestDTO {

    private String title;
    private String content;

    public UpdateMeetupRequestDTO(Meetup meetup) {
        this.title = meetup.getTitle();
        this.content = meetup.getContent();
    }
}
