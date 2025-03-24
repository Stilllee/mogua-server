package com.fesi6.team1.study_group.domain.review.service;

import com.fesi6.team1.study_group.domain.meetup.entity.MeetingType;
import com.fesi6.team1.study_group.domain.meetup.entity.Meetup;
import com.fesi6.team1.study_group.domain.meetup.entity.MeetupUser;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupService;
import com.fesi6.team1.study_group.domain.meetup.service.MeetupUserService;
import com.fesi6.team1.study_group.domain.review.dto.CreateReviewRequestDTO;
import com.fesi6.team1.study_group.domain.review.dto.ReviewResponseDTO;
import com.fesi6.team1.study_group.domain.review.dto.ReviewResponseDTOList;
import com.fesi6.team1.study_group.domain.review.dto.UpdateReviewRequestDTO;
import com.fesi6.team1.study_group.domain.review.entity.Review;
import com.fesi6.team1.study_group.domain.review.repository.ReviewRepository;
import com.fesi6.team1.study_group.domain.meetup.dto.UserEligibleReviewResponseDTO;
import com.fesi6.team1.study_group.domain.meetup.dto.UserEligibleReviewResponseDTOList;
import com.fesi6.team1.study_group.domain.review.dto.UserWrittenReviewResponseDTO;
import com.fesi6.team1.study_group.domain.review.dto.UserWrittenReviewResponseDTOList;
import com.fesi6.team1.study_group.domain.user.dto.UserTutoringReviewResponseDTO;
import com.fesi6.team1.study_group.domain.user.dto.UserTutoringReviewResponseDTOList;
import com.fesi6.team1.study_group.domain.user.entity.User;
import com.fesi6.team1.study_group.domain.user.service.UserService;
import com.fesi6.team1.study_group.global.common.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MeetupService meetupService;
    private final MeetupUserService meetupUserService;
    private final UserService userService;
    private final S3FileService s3FileService;

    public void saveReview(CreateReviewRequestDTO request, Long userId, MultipartFile image) throws IOException {

        String path = "reviewImage";
        String fileName = null;
        String basePath = "https://fesi6.s3.dualstack.ap-southeast-2.amazonaws.com/reviewImage/";

        if (image != null && !image.isEmpty()) {
            String uploadedFileName = s3FileService.uploadFile(image, path);
            fileName = basePath + uploadedFileName;
        }

        Long meetupId = request.getMeetupId();
        MeetupUser meetupUser = meetupUserService.findByMeetupIdAndUserId(meetupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임에 대한 사용자가 존재하지 않습니다."));
        if (meetupUser.isHasReview()) {
            throw new IllegalStateException("이미 리뷰를 작성하셨습니다.");
        }

        Meetup meetup = meetupService.findMeetupById(meetupId);
        User user = userService.findById(userId);

        Review review = Review.builder()
                .meetup(meetup)
                .user(user)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        if (fileName != null) {
            review.setThumbnail(fileName);
        }
        reviewRepository.save(review);
        meetupUser.setHasReview(true);
        meetupUserService.save(meetupUser);
    }


    public ReviewResponseDTO getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        return new ReviewResponseDTO(review);
    }


    public void updateReview(UpdateReviewRequestDTO request, Long reviewId, Long userId, MultipartFile image) throws IOException {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));;
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("리뷰를 수정할 권한이 없습니다");
        }
        if (image != null) {
            String path = "reviewImage";
            String basePath = "https://fesi6.s3.dualstack.ap-southeast-2.amazonaws.com/reviewImage/";
            String currentThumbnail = review.getThumbnail();

            boolean isDefaultImage = currentThumbnail != null && currentThumbnail.equals(basePath + "defaultProfileImages.png");

            if (!isDefaultImage && currentThumbnail != null) {
                String oldFilePath = currentThumbnail.replace(basePath, "");
                s3FileService.deleteFile(oldFilePath);
            }
            String uploadedFileName = s3FileService.uploadFile(image, path);
            review.setThumbnail(basePath + uploadedFileName);
        }
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("리뷰를 삭제할 권한이 없습니다.");
        }

        String currentThumbnail = review.getThumbnail();
        if (currentThumbnail != null && !currentThumbnail.isEmpty()) {
            String basePath = "https://fesi6.s3.dualstack.ap-southeast-2.amazonaws.com/reviewImage/";
            String oldFilePath = currentThumbnail.replace(basePath, "");
            s3FileService.deleteFile(oldFilePath);
        }

        reviewRepository.delete(review);

        MeetupUser meetupUser = meetupUserService.findByMeetupIdAndUserId(
                        review.getMeetup().getId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임에 대한 사용자가 존재하지 않습니다."));
        meetupUser.setHasReview(false);
        meetupUserService.save(meetupUser);
    }

    public ReviewResponseDTOList getReviewsByMeetupId(Long meetupId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.asc("createdAt")));
        Page<Review> reviewPage = reviewRepository.findByMeetupId(meetupId, pageable);

        List<ReviewResponseDTO> reviewResponseDTOList = reviewPage.getContent().stream()
                .map(review -> new ReviewResponseDTO(review))
                .collect(Collectors.toList());

        Integer nextPage = reviewPage.hasNext() ? page + 1 : -1;
        boolean isLast = reviewPage.isLast();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("nextPage", nextPage);
        additionalData.put("isLast", isLast);

        return new ReviewResponseDTOList(reviewResponseDTOList, additionalData);
    }

    public Page<Review> findByUserIdAndType(Long userId, MeetingType type, Pageable pageable) {
        return reviewRepository.findByUserIdAndType(userId, type, pageable);
    }

    public UserEligibleReviewResponseDTOList getUserEligibleReviewResponse(Long userId, MeetingType type, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.asc("createdAt")));

        Page<MeetupUser> meetupUserPage = meetupUserService.findEligibleReviews(userId, type, pageable);

        List<UserEligibleReviewResponseDTO> userEligibleReviewResponseDTOList = meetupUserPage.getContent().stream()
                .map(meetupUser -> {
                    Meetup meetup = meetupUser.getMeetup();
                    meetup.updateStatusIfNeeded();
                    return new UserEligibleReviewResponseDTO(
                            meetup,
                            meetupUserService.getParticipantsCount(meetup.getId())
                    );
                })
                .collect(Collectors.toList());

        Integer nextPage = meetupUserPage.hasNext() ? page + 1 : -1;
        boolean isLast = meetupUserPage.isLast();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("nextPage", nextPage);
        additionalData.put("isLast", isLast);

        return new UserEligibleReviewResponseDTOList(userEligibleReviewResponseDTOList, additionalData);
    }


    public UserWrittenReviewResponseDTOList getUserWrittenReviewResponse(Long userId, MeetingType type, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.desc("createdAt")));

        Page<Review> reviewPage = reviewRepository.findByUserIdAndMeetingType(userId, type, pageable);

        List<UserWrittenReviewResponseDTO> userWrittenReviewResponseDTOList = reviewPage.getContent().stream()
                .map(review -> {
                    LocalDateTime reviewPeriodEnd = review.getMeetup().getMeetingEndDate().plusDays(5);
                    boolean isEditable = review.getCreatedAt().isBefore(reviewPeriodEnd);
                    review.setEditable(isEditable);
                    return new UserWrittenReviewResponseDTO(review);
                })
                .collect(Collectors.toList());

        Integer nextPage = reviewPage.hasNext() ? page + 1 : -1;
        boolean isLast = reviewPage.isLast();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("nextPage", nextPage);
        additionalData.put("isLast", isLast);
        return new UserWrittenReviewResponseDTOList(userWrittenReviewResponseDTOList, additionalData);
    }


    public UserTutoringReviewResponseDTOList getUserTutoringReview(Long userId, Integer page, Integer limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Order.desc("createdAt")));
        MeetingType type = MeetingType.valueOf("TUTORING");
        Page<Review> reviewPage = reviewRepository.findByHostIdAndMeetingType(userId, type, pageable);

        List<UserTutoringReviewResponseDTO> userTutoringReviewResponseDTOList = reviewPage.getContent().stream()
                .map(review -> new UserTutoringReviewResponseDTO(review))
                .collect(Collectors.toList());
        Integer nextPage = reviewPage.hasNext() ? page + 1 : -1;
        boolean isLast = reviewPage.isLast();

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("nextPage", nextPage);
        additionalData.put("isLast", isLast);

        return new UserTutoringReviewResponseDTOList(userTutoringReviewResponseDTOList, additionalData);
    }

}