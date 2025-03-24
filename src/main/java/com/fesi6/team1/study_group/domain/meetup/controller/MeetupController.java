package com.fesi6.team1.study_group.domain.meetup.controller;

import com.fesi6.team1.study_group.domain.meetup.dto.CreateMeetupRequestDTO;
import com.fesi6.team1.study_group.domain.meetup.dto.MeetupListResponseDTO;
import com.fesi6.team1.study_group.domain.meetup.dto.MeetupResponseDTO;
import com.fesi6.team1.study_group.domain.meetup.dto.UpdateMeetupRequestDTO;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupRequestService;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupService;
import com.fesi6.team1.study_group.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetups")
public class MeetupController {

    private final MeetupService meetupService;
    private final MeetupRequestService meetupRequestService;

    /**
     *
     * 모임 생성
     *
     */
    @PostMapping
    public ResponseEntity<?> CreateMeetups(
            @AuthenticationPrincipal Long userId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "request") CreateMeetupRequestDTO request) throws IOException{
        Map<String, Object> responseData = meetupService.saveMeetup(image, request, userId);
        meetupRequestService.requestMeetup((Long) responseData.get("meetupId"), userId);
        return ResponseEntity.ok()
                .body(ApiResponse.successWithDataAndMessage(responseData, "Meetup created successfully"));
    }

    /**
     *
     * 모임 수정
     *
     */
    @PatchMapping("/{meetupId}")
    public ResponseEntity<?> updateMeetups(
            @AuthenticationPrincipal Long userId,
            @PathVariable("meetupId") Long meetupId,
            @RequestPart(value ="image",required = false) MultipartFile image,
            @RequestPart UpdateMeetupRequestDTO request) throws IOException, IllegalAccessException {
        meetupService.updateMeetup(image, request, userId, meetupId);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("모임 수정 성공"));
    }

    /**
     *
     * 모임 삭제
     *
     */
    @DeleteMapping("/{meetupId}")
    public ResponseEntity<?> deleteMeetup(
            @AuthenticationPrincipal Long userId,
            @PathVariable("meetupId") Long meetupId) throws IllegalAccessException {
        meetupService.deleteMeetup(userId, meetupId);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("모임 삭제 성공"));
    }

    /**
     *
     * 모임 단건 조회
     *
     */
    @GetMapping("/{meetupId}")
    public ResponseEntity<ApiResponse<MeetupResponseDTO>> getMeetupById(
            @PathVariable("meetupId") Long meetupId) {
        MeetupResponseDTO response = new MeetupResponseDTO(meetupService.findMeetupByIdWithStatusUpdate(meetupId));
        return ResponseEntity.ok().body(ApiResponse.successResponse(response));
    }

    /**
     *
     * 모임 리스트 조회
     *
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<MeetupResponseDTO>>> getMeetupList(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "orderBy", defaultValue = "latest") String orderBy,
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(value = "location", defaultValue = "ALL") String location,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        MeetupListResponseDTO meetupListResponseDTO = meetupService.getMeetupListWithStatusUpdate(
                page, limit, orderBy, type, state, location, startDate, endDate
        );

        ApiResponse<List<MeetupResponseDTO>> response = ApiResponse.successResponse(
                meetupListResponseDTO.getMeetups(), meetupListResponseDTO.getAdditionalData()
        );
        return ResponseEntity.ok().body(response);
    }

    /**
     *
     * 모든 모임 리스트 조회
     *
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MeetupResponseDTO>>> getAllMeetups() {
        List<MeetupResponseDTO> meetups = meetupService.getAllMeetups();
        ApiResponse<List<MeetupResponseDTO>> response = ApiResponse.successResponse(meetups);
        return ResponseEntity.ok().body(response);
    }


}