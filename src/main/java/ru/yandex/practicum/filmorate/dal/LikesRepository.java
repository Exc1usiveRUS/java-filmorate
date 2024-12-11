package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

@Repository
public class LikesRepository extends BaseRepository<Like> {
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String GET_LIKES_BY_USER_QUERY = "SELECT * FROM FILMS_LIKES WHERE USER_ID = ?";

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Integer filmId, Integer userId) {
        jdbc.update(INSERT_QUERY_OF_FILM, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        jdbc.update(DELETE_QUERY_OF_FILM, filmId, userId);
    }

    public Collection<Like> getLikesByUser(int userId) {
        return findMany(GET_LIKES_BY_USER_QUERY, userId);
    }
}
