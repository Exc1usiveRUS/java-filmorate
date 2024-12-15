package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.model.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private final ReviewRepository reviewRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final FilmRepository filmRepository;
    @Autowired
    private final ReviewsLikesRepository likesRepository;
    @Autowired
    private final EventRepository eventRepository;

    public Review getReviewById(Integer reviewId) {
        return reviewRepository.getReviewById(reviewId);
    }

    public Collection<Review> getAllReviews(Integer filmId, Integer count) {
        Collection<Review> reviews;
        if (filmId != null) {
            reviews = reviewRepository.getReviewsByFilm(filmId).stream()
                    .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                    .toList();
        } else {
            reviews = reviewRepository.getAll();
        }
        if (count == null || count == 0 || count >= reviews.size()) {
            return reviews;
        } else {
            return reviews.stream().limit(count).toList();
        }
    }

    public Review addReview(Review review) {
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());
        Review reviewWithId = reviewRepository.addReview(review);
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), reviewWithId.getUserId(), EventType.REVIEW,
                OperationType.ADD, 0, reviewWithId.getReviewId()));
        return reviewWithId;
    }

    public Review updateReview(Review review) {
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());
        Review updatedReview = reviewRepository.updateReview(review);
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), updatedReview.getUserId(), EventType.REVIEW,
                OperationType.UPDATE, 0, review.getReviewId()));
        return updatedReview;
    }

    public void deleteReview(Integer reviewId) {
        Review review = getReviewById(reviewId);
        reviewRepository.deleteReview(reviewId);
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW,
                OperationType.REMOVE, 0, reviewId));
    }

    public Review addLike(Integer reviewId, Integer userId) {
        userRepository.getUserById(userId);
        Review review = reviewRepository.getReviewById(reviewId);
        if (likesRepository.getLike(reviewId, userId) == null) {
            review.setUseful(review.getUseful() + 1);
            reviewRepository.updateUseful(review.getUseful(), reviewId);
            ReviewLike like = new ReviewLike();
            like.setReviewId(reviewId);
            like.setUserId(userId);
            like.setLike(true);
            likesRepository.addLike(like);
        }
        return review;
    }

    public Review deleteLike(Integer reviewId, Integer userId) {
        userRepository.getUserById(userId);
        Review review = reviewRepository.getReviewById(reviewId);
        if (likesRepository.getLike(reviewId, userId) != null) {
            review.setUseful(review.getUseful() - 1);
            reviewRepository.updateUseful(review.getUseful(), reviewId);
            likesRepository.deleteLike(reviewId, userId);
        }
        return review;
    }

    public Review deleteDislike(Integer reviewId, Integer userId) {
        userRepository.getUserById(userId);
        Review review = reviewRepository.getReviewById(reviewId);
        if (likesRepository.getLike(reviewId, userId) != null) {
            review.setUseful(review.getUseful() + 1);
            reviewRepository.updateUseful(review.getUseful(), reviewId);
            likesRepository.deleteLike(reviewId, userId);
        }
        return review;
    }

    public Review dislike(Integer reviewId, Integer userId) {
        userRepository.getUserById(userId);
        Review review = reviewRepository.getReviewById(reviewId);
        ReviewLike like = likesRepository.getLike(reviewId, userId);
        if (like != null && like.getLike()) {
            likesRepository.updateLike(reviewId, userId, false);
            review.setUseful(review.getUseful() - 2);
        } else if (like == null) {
            like = new ReviewLike();
            like.setReviewId(reviewId);
            like.setUserId(userId);
            like.setLike(false);
            likesRepository.addLike(like);
            review.setUseful(review.getUseful() - 1);
        }
        reviewRepository.updateUseful(review.getUseful(), reviewId);
        return review;
    }

}
