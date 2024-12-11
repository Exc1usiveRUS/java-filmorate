package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Repository
public class EventRepository extends BaseRepository<Event> {
    private static final String USER_EVENTS_QUERY = "SELECT * FROM EVENTS WHERE USER_ID = ?";
    private static final String INSERT_EVENT_QUERY = "INSERT INTO EVENTS (EVENT_TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION_TYPE, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String USER_FRIENDS_QUERY = "SELECT * FROM USERS WHERE USER_ID IN" +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";


    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Event> getEvents(int userId) {
        Collection<Event> events = findMany(USER_EVENTS_QUERY, userId);
        Collection<User> userFriends = jdbc.query(USER_FRIENDS_QUERY, new UserRowMapper(), userId);
        for (User user : userFriends) {
            events.addAll(findMany(USER_EVENTS_QUERY, user.getId()));
        }
        return events;
    }

    public void addEvent(Event event) {
        insert(
                INSERT_EVENT_QUERY,
                Timestamp.from(Instant.ofEpochMilli(event.getTimestamp())),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
    }
}
