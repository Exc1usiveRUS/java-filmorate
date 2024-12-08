package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.ReviewsLikesRowMapper;
import ru.yandex.practicum.filmorate.model.ReviewLike;

@Repository
public class ReviewsLikesRepository {

    private final JdbcTemplate jdbc;
    private static final String INSERT_INTO_REVIEWS_LIKES = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_FROM_REVIEWS_LIKES = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String SELECT_FROM_REVIEWS_LIKES = "SELECT * FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String UPDATE_LIKE = "UPDATE REVIEWS_LIKES SET IS_POSITIVE = ? WHERE REVIEW_ID = ? AND USER_ID = ?";

    public ReviewsLikesRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addLike(ReviewLike like) {
        jdbc.update(INSERT_INTO_REVIEWS_LIKES,
                like.getReviewId(),
                like.getUserId(),
                like.getLike());
    }

    public void deleteLike(Integer reviewId, Integer userId) {
        jdbc.update(DELETE_FROM_REVIEWS_LIKES, reviewId, userId);
    }

    public ReviewLike getLike(Integer reviewId, Integer userId) {
        try {
            return jdbc.queryForObject(SELECT_FROM_REVIEWS_LIKES, new ReviewsLikesRowMapper(), reviewId, userId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void updateLike(Integer reviewId, Integer userId, Boolean status) {
        jdbc.update(UPDATE_LIKE, status, reviewId, userId);
    }
}
