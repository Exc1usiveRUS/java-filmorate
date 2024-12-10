package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@Repository
public class LikesRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String UPDATE_COUNT_LIKES_QUERY = "UPDATE FILMS " +
            "SET COUNT_LIKES = COUNT_LIKES + ? WHERE film_id = ?";
    private static final String FIND_COUNT_LIKES_BY_ID_QUERY = "SELECT COUNT(user_id) " +
            "FROM likes WHERE film_id = ? GROUP BY film_id";

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Film film, User user) {
        jdbc.update(INSERT_QUERY_OF_FILM, film.getId(), user.getId());
        updateCountLikes(1, film);
    }

    public void deleteLike(Film film, User user) {
        jdbc.update(DELETE_QUERY_OF_FILM, film.getId(), user.getId());
        updateCountLikes(-1, film);
    }

    public Integer getCountLikes(Film film) {
        Integer countLikes = jdbc.queryForObject(FIND_COUNT_LIKES_BY_ID_QUERY, Integer.class, film.getId());
        if (countLikes == null) {
            throw new RuntimeException("Не удалось получить количество лайков");
        }
        return countLikes;
    }

    private void updateCountLikes(Integer count, Film film) {
        jdbc.update(UPDATE_COUNT_LIKES_QUERY, count, film.getId());
        film.setCountLikes(count);
    }
}
