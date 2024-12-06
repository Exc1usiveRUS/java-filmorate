package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FilmRepository filmRepository;

    public Review getReviewById(Integer reviewId) {
        return reviewRepository.getReviewById(reviewId);
    }

    public Collection<Review> getAllReviews(Integer filmId, Integer count) {
        List<Review> reviews = reviewRepository.getAll().stream()
                .sorted(Comparator.comparingInt(Review::getUseful))
                .toList().reversed();
        if (filmId != 0) {
            reviews = reviews.stream()
                    .filter(review -> review.getFilmId() == filmId)
                    .toList();
        }
        if (count == 0 || count > reviews.size())
            return reviews;
        else {
            List<Review> topReviews = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                topReviews.add(reviews.get(i));
            }
            return topReviews;
        }
    }

    public Review addReview(Review review) {
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());
        return reviewRepository.addReview(review);
    }

    public Review updateReview(Review review) {
        userRepository.getUserById(review.getUserId());
        filmRepository.getFilmById(review.getFilmId());
        return reviewRepository.updateReview(review);
    }

    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteReview(reviewId);
    }

    public Review addLike(Integer reviewId, Integer userId) {
        userRepository.getUserById(userId);
        Review review = reviewRepository.getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewRepository.updateUseful(review.getUseful(), reviewId);
        return review;
    }

    public Review dislike(Integer reviewId, Integer userId) {
        userRepository.getUserById(userId);
        Review review = reviewRepository.getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewRepository.updateUseful(review.getUseful(), reviewId);
        return review;
    }
}
