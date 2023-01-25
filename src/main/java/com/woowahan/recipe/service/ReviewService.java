package com.woowahan.recipe.service;

import com.woowahan.recipe.domain.UserRole;
import com.woowahan.recipe.domain.dto.reviewDto.*;
import com.woowahan.recipe.domain.entity.*;
import com.woowahan.recipe.event.AlarmEvent;
import com.woowahan.recipe.exception.AppException;
import com.woowahan.recipe.exception.ErrorCode;
import com.woowahan.recipe.repository.AlarmRepository;
import com.woowahan.recipe.repository.RecipeRepository;
import com.woowahan.recipe.repository.ReviewRepository;
import com.woowahan.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final AlarmRepository alarmRepository;
    private final ApplicationEventPublisher publisher;

    // User가 존재하는지 확인한다
    private UserEntity validateUser(String username) {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));
        return user;
    }

    // 댓글을 달 레시피가 존재하는지 확인한다
    private RecipeEntity validateRecipe(Long recipeId)  {
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND, ErrorCode.RECIPE_NOT_FOUND.getMessage()));
        return recipe;
    }

    // 리뷰가 존재하는지 확인
    private ReviewEntity validateReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND, ErrorCode.REVIEW_NOT_FOUND.getMessage()));
        return review;
    }

    // 리뷰 작성
    public ReviewCreateResponse createReview(Long recipeId, ReviewCreateRequest reviewCreateRequest, String username)  {
        // 유저가 존재하는지 확인
        UserEntity user = validateUser(username);

        // 레시피가 존재하는지 확인
        RecipeEntity recipe = validateRecipe(recipeId);

        // 내용이 있는지 확인. 없으면 에러 코드
        if (reviewCreateRequest.getComment().length() == 0) {
            throw new AppException(ErrorCode.EMPTY_CONTENT, ErrorCode.EMPTY_CONTENT.getMessage());
        }

        // 리뷰 저장
        ReviewEntity review = reviewRepository.save(reviewCreateRequest.toEntity(user, recipe, reviewCreateRequest.getComment()));

        // 리뷰 작성자와 레시피 작성자가 일치하지 않다면 알람 등록
        if(!user.equals(recipe.getUser())) {
            publisher.publishEvent(AlarmEvent.of(AlarmType.NEW_REVIEW_ON_RECIPE, user, recipe.getUser()));
        }

        return new ReviewCreateResponse(review.getReviewId(), user.getName(), review.getReview_comment());
    }

    // 리뷰 수정
    public ReviewCreateResponse updateReview(Long recipeId, Long reviewId, ReviewCreateRequest reviewCreateRequest, String username) {
        // 유저가 존재하는지 확인
        UserEntity user = validateUser(username);

        // 레시피가 존재하는지 확인
        RecipeEntity recipe = validateRecipe(recipeId);

        // 리뷰가 존재하는지 확인
        ReviewEntity review = validateReview(reviewId);

        //리뷰 작성자와 유저가 동일한지 확인
        if (!review.getUser().getUserName().equals(username)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
        }

        // 내용이 있는지 확인
        if (reviewCreateRequest.getComment().length() == 0) {
            throw(new AppException(ErrorCode.EMPTY_CONTENT, ErrorCode.EMPTY_CONTENT.getMessage()));
        }

        review.update(reviewCreateRequest.getComment());

        // 저장
        ReviewEntity savedReview = reviewRepository.save(review);

        // 알람 울리도록 저장
        AlarmEntity alarm = AlarmEntity.builder()
                .alarmType(AlarmType.NEW_REVIEW_ON_RECIPE)
                .fromUser(user.getId())
                .targetUser(recipe.getUser())
                .build();

        alarmRepository.save(alarm);

        return new ReviewCreateResponse(savedReview.getReviewId(), user.getUserName(), savedReview.getReview_comment());
    }

    // 리뷰 단건 삭제
   public ReviewDeleteResponse deleteReview(Long recipeId, Long reviewId, String username) {
       // 유저가 존재하는지 확인
       UserEntity user = validateUser(username);

       // 레시피가 존재하는지 확인
       RecipeEntity recipe = validateRecipe(recipeId);

       // 리뷰가 존재하는지 확인
       ReviewEntity review = validateReview(reviewId);

       //관리자거나 리뷰 작성자와 유저가 동일한지 확인
       if (!review.getUser().getUserName().equals(username)
               && (user.getUserRole() != UserRole.ADMIN ||user.getUserRole() != UserRole.HEAD)) {
           throw new AppException(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());
       }

       // soft delete
       reviewRepository.delete(review);
       return new ReviewDeleteResponse(reviewId, "댓글 삭제 완료");
   }

   // 특정 레시피의 리뷰 전체 조회
    public Page<ReviewListResponse> findAllReviews(Long recipeId, Pageable pageable) {
        // 레시피가 존재하는지 확인
        RecipeEntity recipe = validateRecipe(recipeId);

        // 20개씩 만들어진 순으로 정렬
        pageable = PageRequest.of(0, 20, Sort.by("createdDate").descending());

        Page<ReviewEntity> reviews = reviewRepository.findAllByRecipe(recipe, pageable);
        return reviews.map(ReviewListResponse::toList);
    }

    // 특정 유저의 리뷰 전체 조회
    public Page<ReviewListResponse> findAllReviewsByUser(String username, Pageable pageable) {
        // 유저가 존재하는지 확인
        UserEntity user = validateUser(username);

        // 20개씩 만들어진 순으로 정렬
        pageable = PageRequest.of(0, 20, Sort.by("createdDate").descending());

        Page<ReviewEntity> reviews = reviewRepository.findAllByUser(user, pageable);
        return reviews.map(ReviewListResponse::toList);
    }

}
