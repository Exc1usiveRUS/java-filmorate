package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setTimestamp(rs.getTimestamp("EVENT_TIMESTAMP").toInstant().toEpochMilli());
        event.setUserId(rs.getInt("USER_ID"));
        switch (rs.getString("EVENT_TYPE")) {
            case "LIKE":
                event.setEventType(EventType.LIKE);
                break;
            case "FRIEND":
                event.setEventType(EventType.FRIEND);
                break;
            case "REVIEW":
                event.setEventType(EventType.REVIEW);
                break;
        }
        switch (rs.getString("OPERATION_TYPE")) {
            case "ADD":
                event.setOperation(OperationType.ADD);
                break;
            case "REMOVE":
                event.setOperation(OperationType.REMOVE);
                break;
            case "UPDATE":
                event.setOperation(OperationType.UPDATE);
                break;
        }
        event.setEventId(rs.getInt("EVENT_ID"));
        event.setEntityId(rs.getInt("ENTITY_ID"));
        return event;
    }
}
