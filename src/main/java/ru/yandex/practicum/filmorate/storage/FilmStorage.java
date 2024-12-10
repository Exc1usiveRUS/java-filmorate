package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer filmId);

    Collection<Film> getFilms();

    Film getFilmById(Integer filmId);

    Collection<Film> getTopFilms(Integer count);

    List<Film> getFilmsByDirectorId(Integer directorId);
}
