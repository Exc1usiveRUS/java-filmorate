package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer filmId);

    Collection<Film> getFilms();

    Film getFilmById(Integer filmId);

    Collection<Film> filmsSearch(String title, List<String> field);

    Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getFilmsByDirectorId(Integer directorId);
}
