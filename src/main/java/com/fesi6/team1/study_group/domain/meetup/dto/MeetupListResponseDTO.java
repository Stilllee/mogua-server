package com.fesi6.team1.study_group.domain.meetup.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class MeetupListResponseDTO {
    private List<MeetupResponseDTO> meetups;
    private Map<String, Object> additionalData;

    public MeetupListResponseDTO(List<MeetupResponseDTO> meetups, Map<String, Object> additionalData) {
        this.meetups = meetups;
        this.additionalData = additionalData;
    }
}

