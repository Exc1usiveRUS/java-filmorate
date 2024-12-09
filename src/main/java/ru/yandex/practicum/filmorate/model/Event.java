package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Comparable<Event> {
    private long timestamp;
    private long userId;
    private EventType eventType;
    private OperationType operation;
    private long eventId;
    private long entityId;

    @Override
    public int compareTo(Event event) {
        return (int) event.timestamp - (int) this.timestamp;
    }
}



