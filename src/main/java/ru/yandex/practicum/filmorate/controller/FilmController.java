package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавлен фильм: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film updatedFilm) {
        if (!films.containsKey(updatedFilm.getId())) {
            throw new ValidationException("Фильм c таким id не найден");
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
        log.info("Обновлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}