package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

@Repository
public class LikesRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Integer filmId, Integer userId) {
        jdbc.update(INSERT_QUERY_OF_FILM, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        jdbc.update(DELETE_QUERY_OF_FILM, filmId, userId);
    }
}
