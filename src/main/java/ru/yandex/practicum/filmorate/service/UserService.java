package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (userId == friendId) {
            log.error("Нельзя добавить в друзья самого себя");
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        if (user.getFriends().contains(friendId)) {
            log.info("Пользователь с id {} уже в друзьях", friendId);
            throw new ValidationException("Пользователь с id " + friendId + " уже в друзьях");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().remove(friendId)) {
            log.info("Пользователь с id {} удален из друзей", friendId);
        }
        friend.getFriends().remove(userId);

    }

    public Collection<User> getCommonFriends(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        Set<Integer> commons = new HashSet<>(user.getFriends());

        if (commons.retainAll(friend.getFriends())) {
            return commons.stream()
                    .map(userStorage::getUserById)
                    .toList();
        } else {
            throw new NotFoundException("Нет общих друзей");
        }
    }

    public Collection<User> getFriends(int userId) {
        return userStorage.getUserById(userId).getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }
}
