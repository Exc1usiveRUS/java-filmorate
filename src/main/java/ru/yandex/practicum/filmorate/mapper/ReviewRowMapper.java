package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("REVIEW_ID"));
        review.setContent(rs.getString("CONTENT"));
        review.setIsPositive(rs.getBoolean("IS_POSITIVE"));
        review.setUserId(rs.getInt("USER_ID"));
        review.setFilmId(rs.getInt("FILM_ID"));
        review.setUseful(rs.getInt("USEFUL"));
        return review;
    }
}
