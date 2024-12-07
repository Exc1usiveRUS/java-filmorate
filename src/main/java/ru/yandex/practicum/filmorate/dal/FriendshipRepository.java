package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;

@Repository
public class FriendshipRepository extends BaseRepository<User> {
    private static final String INSERT_QUERY = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM FRIENDS WHERE FRIEND_ID = ? AND USER_ID = ?";
    private static final String QUERY_FOR_USER_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN" +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
    private static final String QUERY_FOR_COMMON_FRIENDS = "SELECT * FROM USERS WHERE USER_ID IN " +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?) AND USER_ID IN " +
            "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";

    @Autowired
    EventRepository eventRepository;

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<User> mapper, EventRepository eventRepository) {
        super(jdbc, mapper);
        this.eventRepository = eventRepository;
    }

    public void addFriend(Integer userId, Integer friendId) {
        update(INSERT_QUERY, userId, friendId);
        //записываем добавления друга в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND, OperationType.ADD, 0, friendId));
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        update(DELETE_QUERY, friendId, userId);
        //записываем удаление друга в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND, OperationType.REMOVE, 0, friendId));
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        return findMany(QUERY_FOR_COMMON_FRIENDS, userId, friendId);
    }

    public Collection<User> getAllFriends(Integer userId) {
        return findMany(QUERY_FOR_USER_FRIENDS, userId);
    }
}
