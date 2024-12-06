package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Repository
public class ReviewRepository extends BaseRepository<Review>{

    private static final String SELECT_ALL_REVIEWS = "SELECT * FROM REVIEWS";
    private static final String SELECT_BY_ID = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String INSERT_INTO_REVIEWS = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_REVIEW = "UPDATE REVIEWS SET " +
            "CONTENT = ?, IS_POSITIVE = ?, USER_ID = ?, FILM_ID = ? WHERE REVIEW_ID = ?";
    private static final String DELETE_REVIEW = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String UPDATE_USEFUL = "UPDATE REVIEWS SET USEFUL = ? WHERE REVIEW_ID = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Review> getAll() {
        return findMany(SELECT_ALL_REVIEWS);
    }

    public Review getReviewById(int reviewId) {
        return findOne(SELECT_BY_ID, reviewId);
    }

    public Review addReview(Review review) {
        review.setReviewId(insert(INSERT_INTO_REVIEWS,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0));
        review.setUseful(0);
        return review;
    }

    public Review updateReview(Review review) {
        update(UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
        if (review.getUseful() == null)
            review.setUseful(0);
        return review;
    }

    public void deleteReview(Integer reviewId) {
        delete(DELETE_REVIEW, reviewId);
    }

    public void updateUseful(Integer useful, Integer reviewId) {
        update(UPDATE_USEFUL, useful, reviewId);
    }

}
