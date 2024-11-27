package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int nextId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film addFilm(Film film) {
        int id = getNextId();
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId())) {
            throw new NotFoundException("Фильм c таким id не найден");
        }
        Film film = films.get(updatedFilm.getId());
        if (updatedFilm.getName() != null && !updatedFilm.getName().isBlank()) {
            film.setName(updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null && !updatedFilm.getDescription().isBlank()) {
            film.setDescription(updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            film.setReleaseDate(updatedFilm.getReleaseDate());
        }
        if (updatedFilm.getDuration() >= 0) {
            film.setDuration(updatedFilm.getDuration());
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Collection<Film> getTopFilms(Integer count) {
        return new ArrayList<>(films.values());
    }
    @Override
    public Film getFilmById(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
        return films.get(filmId);
    }

    @Override
    public void deleteFilm(Integer filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
        films.remove(filmId);
    }

    private int getNextId() {
        return nextId++;
    }
}
