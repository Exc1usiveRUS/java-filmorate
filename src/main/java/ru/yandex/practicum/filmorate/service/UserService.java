package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;
    private final EventRepository eventRepository;

    public UserService(@Autowired @Qualifier("userRepository") UserStorage userStorage,
                       @Autowired FriendshipRepository friendshipRepository,
                       @Autowired EventRepository eventRepository) {
        this.userStorage = userStorage;
        this.friendshipRepository = friendshipRepository;
        this.eventRepository = eventRepository;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendshipRepository.addFriend(userId, friendId);
        //записываем добавления друга в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND, OperationType.ADD, 0, friendId));
    }

    public void deleteFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendshipRepository.deleteFriend(userId, friendId);
        //записываем удаление друга в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND, OperationType.REMOVE, 0, friendId));
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);
        return friendshipRepository.getCommonFriends(userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        getUserById(userId);
        return friendshipRepository.getAllFriends(userId);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }
}
