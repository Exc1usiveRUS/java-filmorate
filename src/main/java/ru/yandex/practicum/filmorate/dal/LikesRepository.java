package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.time.Instant;

@Repository
public class LikesRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    @Autowired
    EventRepository eventRepository;

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, EventRepository eventRepository) {
        super(jdbc, mapper);
        this.eventRepository = eventRepository;
    }

    public void addLike(Integer filmId, Integer userId) {
        jdbc.update(INSERT_QUERY_OF_FILM, filmId, userId);
        //записываем добавление лайка в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE, OperationType.ADD, 0, filmId));
    }

    public void deleteLike(Integer filmId, Integer userId) {
        jdbc.update(DELETE_QUERY_OF_FILM, filmId, userId);
        //записываем удаление лайка в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE, OperationType.REMOVE, 0, filmId));
    }
}
