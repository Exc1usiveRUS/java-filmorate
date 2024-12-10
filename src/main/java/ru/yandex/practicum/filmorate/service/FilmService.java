package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;

    public FilmService(@Autowired @Qualifier("filmRepository") FilmStorage filmStorage,
                       @Autowired GenreRepository genreRepository,
                       @Autowired LikesRepository likesRepository) {
        this.filmStorage = filmStorage;
        this.genreRepository = genreRepository;
        this.likesRepository = likesRepository;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        likesRepository.addLike(filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.getFilmById(filmId);
        likesRepository.deleteLike(filmId, userId);
        log.info("Пользователь {} отменил лайк фильма {}", userId, filmId);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
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
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Фильм c таким id не найден");
        }
        Film updatedFilm = filmStorage.updateFilm(film);
        if (!updatedFilm.getGenres().isEmpty()) {
            genreRepository.deleteGenres(updatedFilm.getId());
            genreRepository.addGenres(updatedFilm.getId(), updatedFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        return updatedFilm;
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }
}
