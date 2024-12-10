package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;
    private final UserStorage userStorage;
    private final DirectorRepository directorRepository;

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        likesRepository.addLike(film, user);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        likesRepository.deleteLike(film, user);
        log.info("Пользователь {} отменил лайк фильма {}", userId, filmId);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getFilmsByDirectorId(Integer directorId, String sortBy) {
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    public Collection<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        Film createdFilm = filmStorage.addFilm(film);
        if (!createdFilm.getGenres().isEmpty()) {
            genreRepository.addGenres(createdFilm.getId(), createdFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        if (!createdFilm.getDirectors().isEmpty()) {
            directorRepository.addDirectors(createdFilm.getId(), createdFilm.getDirectors()
                    .stream()
                    .map(Director::getId)
                    .toList());
        }
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Фильм c таким id не найден");
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        if (film.getName() != null) updatedFilm.setName(film.getName());
        if (film.getDescription() != null) updatedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) updatedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) updatedFilm.setDuration(film.getDuration());
        if (film.getMpa() != null) updatedFilm.setMpa(film.getMpa());
        if (film.getLikes() != null) updatedFilm.setLikes(film.getLikes());
        if (!updatedFilm.getGenres().isEmpty()) {
            genreRepository.deleteGenres(updatedFilm.getId());
            genreRepository.addGenres(updatedFilm.getId(), updatedFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        if (!updatedFilm.getDirectors().isEmpty()) {
            directorRepository.deleteDirectorsFromFilm(updatedFilm.getId());
            directorRepository.addDirectors(updatedFilm.getId(), updatedFilm.getDirectors()
                    .stream()
                    .map(Director::getId)
                    .toList());
        }
        return updatedFilm;
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }
}
