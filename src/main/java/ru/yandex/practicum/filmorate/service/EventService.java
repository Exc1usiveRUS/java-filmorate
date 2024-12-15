package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    @Autowired
    private final EventRepository eventRepository;
    private final UserStorage userStorage;

    public Collection<Event> getFeed(int userId) {
        userStorage.getUserById(userId);
        return eventRepository.getEvents(userId);
    }
}
