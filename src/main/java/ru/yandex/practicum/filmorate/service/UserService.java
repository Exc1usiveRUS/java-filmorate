package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;
    private final LikesRepository likesRepository;
    private final FilmRepository filmRepository;

    public UserService(@Autowired @Qualifier("userRepository") UserStorage userStorage,
                       @Autowired FriendshipRepository friendshipRepository,
                       @Autowired LikesRepository likesRepository,
                       @Autowired FilmRepository filmRepository
    ) {
        this.userStorage = userStorage;
        this.friendshipRepository = friendshipRepository;
        this.likesRepository = likesRepository;
        this.filmRepository = filmRepository;
    }

    public Collection<Film> getRecommendations(int userId) {
        // создаем множество для хранения id-шников фильмов для рекомендаций
        Set<Integer> filmIdsForRecommendation = new HashSet<>();
        // получаем множество id-шников фильмов, лайкнутых пользователем
        Set<Integer> filmsLikedByUser = new HashSet<>(likesRepository.getLikesByUser(userId).
                stream().map(Like::getFilmId).toList());
        // создаем мапу, куда будем записывать id пользователей и количство лайков, совпадающих с нужным пользователем
        HashMap<Integer, Integer> usersLikes = new HashMap<>();
        // перебор всех пользователей
        for (User user : getUsers()) {
            // отсекаем пользователя, которому ищем рекомендации
            if (user.getId() != userId) {
                int matches = 0;
                // получаем множество id-шников фильмов, лайкнутых другим пользователем
                Set<Integer> filmsLikedByOtherUser = new HashSet<>(likesRepository.getLikesByUser(user.getId()).
                        stream().map(Like::getFilmId).toList());
                // перебираем id-шники лайкнутых фильмов пользователя и при совпадени с другим пользователем
                // увеличиваем счетчик matches на 1
                for (int i : filmsLikedByUser) {
                    if (filmsLikedByOtherUser.contains(i)) {
                        matches++;
                    }
                }
                // если совпадений нет, не нужно записывать этого пользователя в мапу, так как там должны храниться
                // только те, с которыми есть совпадения.
                if (matches != 0) {
                    usersLikes.put(user.getId(), matches);
                }
            }
        }
        // создаем множество фильмов для рекомендаций
        Collection<Film> recommendedFilms = new HashSet<>();
        // проверка мапы на null, в случае, если она пуста, сразу возвращаем пустое множество
        if (!usersLikes.isEmpty()) {
            // если мапа не пуста, получаем максимальное количество совпадений
            int maxLikesMatches = usersLikes.values().stream().sorted().toList().getFirst();
            // перебор по мапе с пользователями, с которыми есть совпадения по лайкам
            for (int user : usersLikes.keySet()) {
                // ищем только максимальное количество совпадений, в случае если таких пользователей несколько
                if (usersLikes.get(user) == maxLikesMatches) {
                    // добавляем в множество все id-шники фильмов, лайкнутых другими пользователями, с которыми
                    // найдено максимальное количество совпадений
                    filmIdsForRecommendation.addAll(likesRepository.getLikesByUser(user).
                            stream().map(Like::getFilmId).toList());
                }
            }
            // удаляем из рекомендаций все id-шники фильмов, лайкнутых самим пользователем
            filmIdsForRecommendation.removeAll(filmsLikedByUser);
            // заполняем коллекцию фильмами по id-шникам из множества рекомендаций
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
    }

    public void deleteFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
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
