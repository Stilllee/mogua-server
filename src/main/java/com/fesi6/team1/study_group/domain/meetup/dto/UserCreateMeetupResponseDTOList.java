package com.fesi6.team1.study_group.domain.meetup.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class UserCreateMeetupResponseDTOList {
    private List<UserCreateMeetupResponseDTO> userCreateMeetupList;
    private Map<String, Object> additionalData;

    public UserCreateMeetupResponseDTOList(List<UserCreateMeetupResponseDTO> userCreateMeetupList, Map<String, Object> additionalData){
        this.userCreateMeetupList = userCreateMeetupList;
        this.additionalData = additionalData;
    }
}
