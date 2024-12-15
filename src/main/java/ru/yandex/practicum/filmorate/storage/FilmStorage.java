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

    Collection<Film> filmsSearch(String title, boolean byTitle, boolean biDirector);

    Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getFilmsByDirectorId(Integer directorId);
}
