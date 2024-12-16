package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Repository
public class EventRepository extends BaseRepository<Event> {
    private static final String USER_EVENTS_QUERY = "SELECT * FROM EVENTS WHERE USER_ID = ?";
    private static final String INSERT_EVENT_QUERY = "INSERT INTO EVENTS (EVENT_TIMESTAMP, USER_ID, EVENT_TYPE, " +
            "OPERATION_TYPE, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Event> getEvents(int userId) {
        return findMany(USER_EVENTS_QUERY, userId);
    }

    public void addEvent(Event event) {
        int eventId = insert(
                INSERT_EVENT_QUERY,
                Timestamp.from(Instant.ofEpochMilli(event.getTimestamp())),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
        event.setEventId(eventId);
    }
}
