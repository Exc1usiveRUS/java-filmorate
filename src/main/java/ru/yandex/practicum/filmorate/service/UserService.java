package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;

    public UserService(@Autowired @Qualifier("userRepository") UserStorage userStorage,
                       @Autowired FriendshipRepository friendshipRepository) {
        this.userStorage = userStorage;
        this.friendshipRepository = friendshipRepository;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendshipRepository.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        getUserById(friendId);

        if (user.getFriends().remove(friendId)) {
            log.info("Пользователь с id {} удален из друзей", friendId);
        }
        friendshipRepository.deleteFriend(userId, friendId);
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
