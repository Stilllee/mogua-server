package com.fesi6.team1.study_group.domain.review.controller;

import com.fesi6.team1.study_group.domain.review.dto.CreateReviewRequestDTO;
import com.fesi6.team1.study_group.domain.review.dto.ReviewResponseDTO;
import com.fesi6.team1.study_group.domain.review.dto.ReviewResponseDTOList;
import com.fesi6.team1.study_group.domain.review.dto.UpdateReviewRequestDTO;
import com.fesi6.team1.study_group.domain.review.service.ReviewService;
import com.fesi6.team1.study_group.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     *
     * 리뷰 작성
     *
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @AuthenticationPrincipal Long userId,
            @RequestPart(value = "image",required = false) MultipartFile image,
            @RequestPart(value = "request") CreateReviewRequestDTO request) throws Exception {

        reviewService.saveReview(request, userId, image);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("Review created successfully"));
    }

    /**
     *
     * 리뷰 단건 조회
     *
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviews(
            @PathVariable Long reviewId
    ) {
        ReviewResponseDTO response = reviewService.getReview(reviewId);
        return ResponseEntity.ok().body(response);
    }

    /**
     *
     * 리뷰 리스트 조회
     *
     */
    @GetMapping("/list/{meetupId}")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getReviewsByMeetupId(
            @PathVariable Long meetupId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer limit) {

        ReviewResponseDTOList reviewResponseDTOList = reviewService.getReviewsByMeetupId(meetupId, page, limit);

        ApiResponse<List<ReviewResponseDTO>> response = ApiResponse.successResponse(
                reviewResponseDTOList.getReview(), reviewResponseDTOList.getAdditionalData()
        );
        return ResponseEntity.ok().body(response);
    }

    /**
     *
     * 리뷰 수정
     *
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reviewId,
            @RequestPart(required = false) MultipartFile image,
            @RequestPart UpdateReviewRequestDTO request) throws Exception {
        reviewService.updateReview(request, reviewId, userId, image);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("Review updated successfully"));
    }

    /**
     *
     * 리뷰 삭제
     *
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reviewId) throws IOException {

        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("Review deleted successfully"));
    }

}