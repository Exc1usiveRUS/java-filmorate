package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public class LikesRepository extends BaseRepository<Like> {
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String GET_LIKES_BY_FILM_QUERY = "SELECT * FROM FILMS_LIKES WHERE FILM_ID = ?";

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Film film, User user) {
        jdbc.update(INSERT_QUERY_OF_FILM, film.getId(), user.getId());
    }

    public void deleteLike(Film film, User user) {
        jdbc.update(DELETE_QUERY_OF_FILM, film.getId(), user.getId());
    }

    public Collection<Like> getLikesByFilm(int filmId) {
        return findMany(GET_LIKES_BY_FILM_QUERY, filmId);
    }
}
