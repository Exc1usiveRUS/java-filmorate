package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;
    private final EventRepository eventRepository;
    private final DirectorRepository directorRepository;
    private final UserStorage userStorage;


    public void addLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        likesRepository.addLike(filmId, userId);
        //записываем добавление лайка в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE, OperationType.ADD, 0, filmId));
        log.info("User {} liked film {}", userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        likesRepository.deleteLike(filmId, userId);
        //записываем удаление лайка в БД событий
        eventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE, OperationType.REMOVE, 0, filmId));
        log.info("Пользователь {} отменил лайк фильма {}", userId, filmId);
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        if (directorRepository.findDirectorsByFilm(id) != null)
            film.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(film.getId())));
        return film;
    }

    public Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    public List<Film> getFilmsByDirectorId(Integer directorId, String sortBy) {
        directorRepository.findById(directorId);
        List<Film> directorFilms = filmStorage.getFilmsByDirectorId(directorId);
        for (Film film : directorFilms) {
            film.setDirectors(new HashSet<>(directorRepository.findDirectorsByFilm(film.getId())));
            film.setGenres(new HashSet<>(genreRepository.getGenresByFilm(film.getId())));
            film.setLikes(likesRepository.getLikesByFilm(film.getId()).size());
        }
        if (sortBy.equals("year")) {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getReleaseDate)).toList();
        } else {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getLikes).reversed()).toList();
        }
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    //Обратить внимание на сортировку, возможно получится сделать лучше!
    public Film addFilm(Film film) {
        Film createdFilm = filmStorage.addFilm(film);
        if (!createdFilm.getGenres().isEmpty()) {
            genreRepository.addGenres(createdFilm.getId(), createdFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        film.setGenres(film.getGenres()
                .stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        if (!createdFilm.getDirectors().isEmpty()) {
            directorRepository.addDirectors(createdFilm.getId(), createdFilm.getDirectors()
                    .stream()
                    .map(Director::getId)
                    .toList());
        }
        return createdFilm;
    }

    //Обратить внимание на сортировку, возможно получится сделать лучше!
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
        if (!updatedFilm.getGenres().isEmpty()) {
            genreRepository.deleteGenres(updatedFilm.getId());
            genreRepository.addGenres(updatedFilm.getId(), updatedFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
            updatedFilm.setGenres(updatedFilm.getGenres()
                    .stream()
                    .sorted(Comparator.comparingInt(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        } else
            genreRepository.deleteGenres(updatedFilm.getId());
        if (!updatedFilm.getDirectors().isEmpty()) {
            directorRepository.deleteDirectorsFromFilm(updatedFilm.getId());
            directorRepository.addDirectors(updatedFilm.getId(), updatedFilm.getDirectors()
                    .stream()
                    .map(Director::getId)
                    .toList());
        } else
            directorRepository.deleteDirectorsFromFilm(updatedFilm.getId());
        return updatedFilm;
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public Collection<Film> filmSearch(String substring, List<String> paramsList) {
        return filmStorage.filmsSearch(substring, paramsList);
    }
}
