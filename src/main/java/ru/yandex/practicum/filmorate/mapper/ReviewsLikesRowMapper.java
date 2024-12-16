package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewsLikesRowMapper implements RowMapper<ReviewLike> {

    @Override
    public ReviewLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReviewLike like = new ReviewLike();
        like.setReviewId(rs.getInt("REVIEW_ID"));
        like.setUserId(rs.getInt("USER_ID"));
        like.setLike(rs.getBoolean("IS_POSITIVE"));
        return like;
    }
}
