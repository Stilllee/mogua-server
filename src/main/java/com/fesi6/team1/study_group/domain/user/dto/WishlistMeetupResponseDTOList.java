package com.fesi6.team1.study_group.domain.user.dto;

import com.fesi6.team1.study_group.domain.meetup.dto.UserEligibleReviewResponseDTO;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class WishlistMeetupResponseDTOList {
    private List<WishlistMeetupResponseDTO> userWishlist;
    private Map<String, Object> additionalData;

    public WishlistMeetupResponseDTOList(List<WishlistMeetupResponseDTO> meetup, Map<String, Object> additionalData) {
        this.userWishlist = meetup;
        this.additionalData = additionalData;
    }
}
