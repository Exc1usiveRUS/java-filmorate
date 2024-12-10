package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikeRowMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        Like like = new Like();
        like.setUserId(rs.getInt("USER_ID"));
        like.setFilmId(rs.getInt("FILM_ID"));
        return like;
    }
}
