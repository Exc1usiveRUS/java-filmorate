package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final FriendshipRepository friendshipRepository;
    @Autowired
    private final LikesRepository likesRepository;
    @Autowired
    private final FilmRepository filmRepository;
    @Autowired
    private final EventRepository eventRepository;

    public Collection<Film> getRecommendations(int userId) {
        Set<Integer> filmIdsForRecommendation = new HashSet<>();
        Set<Integer> filmsLikedByUser = new HashSet<>(likesRepository.getLikesByUser(userId)
                .stream().map(Like::getFilmId).toList());
        HashMap<Integer, Integer> usersLikes = new HashMap<>();
        for (User user : getUsers()) {
            if (user.getId() != userId) {
                int matches = 0;
                Set<Integer> filmsLikedByOtherUser = new HashSet<>(likesRepository.getLikesByUser(user.getId())
                        .stream().map(Like::getFilmId).toList());
                for (int i : filmsLikedByUser) {
                    if (filmsLikedByOtherUser.contains(i)) {
                        matches++;
                    }
                }
                if (matches != 0) {
                    usersLikes.put(user.getId(), matches);
                }
            }
        }
        Collection<Film> recommendedFilms = new HashSet<>();
        if (!usersLikes.isEmpty()) {
            int maxLikesMatches = usersLikes.values().stream().sorted().toList().getFirst();
            for (int user : usersLikes.keySet()) {
                if (usersLikes.get(user) == maxLikesMatches) {
                    filmIdsForRecommendation.addAll(likesRepository.getLikesByUser(user)
                            .stream().map(Like::getFilmId).toList());
                }
            }
            filmIdsForRecommendation.removeAll(filmsLikedByUser);
            if (!filmIdsForRecommendation.isEmpty()) {
                for (int filmId : filmIdsForRecommendation) {
                    recommendedFilms.add(filmRepository.getFilmById(filmId));
                }
            }
        }
        return recommendedFilms;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendshipRepository.addFriend(userId, friendId);
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND, OperationType.ADD, 0, friendId));
    }

    public void deleteFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendshipRepository.deleteFriend(userId, friendId);
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
