package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {
    private ValidatorFactory validatorFactory;
    private Validator validator;

    FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        filmController = new FilmController();
    }

    @AfterEach
    public void afterEach() {
        validatorFactory.close();
    }

    @Test
    public void testCreateFilm() {
        filmController.addFilm(
                Film.builder()
                        .name("Test film")
                        .description("Test description")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(100)
                        .build()
        );
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void testUpdateFilm() {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        filmController.addFilm(film);
        assertEquals(1, filmController.getFilms().size());
        filmController.updateFilm(Film.builder()
                .id(1)
                .name("Updated film")
                .build());
        assertEquals(1, filmController.getFilms().size());
        assertEquals("Updated film", filmController.getFilms().stream().toList().getFirst().getName());
    }

    @Test
    public void errorCreateFilmWithEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("Test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    public void errorCreateFilmWithNullName() {
        Film film = Film.builder()
                .name(null)
                .description("Test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    public void errorCreateFilmWithNegativeDuration() {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("duration"))
                .count());
    }

    @Test
    public void errorCreateFilmWithZeroDuration() {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(0)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("duration"))
                .count());
    }

    @Test
    public void errorCreateFilmWithNullDuration() {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("duration"))
                .count());
    }

    @Test
    public void errorCreateFilmWithEmptyDescription() {
        Film film = Film.builder()
                .name("Test film")
                .description("")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    public void errorCreateFilmWithNullDescription() {
        Film film = Film.builder()
                .name("Test film")
                .description(null)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    public void errorCreateFilmWithTooLongDescription() {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description".repeat(300))
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    public void ErrorCreateFilmWithInvalidReleaseDate() {
        Film film = Film.builder()
                .name("Test film")
                .description("Test description")
                .releaseDate(LocalDate.of(1800, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("releaseDate"))
                .count());
    }
}
