package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeEach
    void beforeEach() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void afterEach() {
        validatorFactory.close();
    }

    @Test
    void createFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void errorCreateEmptyFilm() {
        Film film = Film.builder().build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void errorCreateNullNameFilm() {
        Film film = Film.builder()
                .name(null)
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    void errorCreateBlankNameFilm() {
        Film film = Film.builder()
                .name(" ")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    void errorCreateEmptyNameFilm() {
        Film film = Film.builder()
                .name("")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    void errorCreateNullDescriptionFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description(null)
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateBlankDescriptionFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description(" ")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateEmptyDescriptionFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateDescriptionTooLongFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("O".repeat(300))
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateNullReleaseDateFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(null)
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("releaseDate"))
                .count());
    }

    @Test
    void errorCreateReleaseDateBeforeFilmsDateFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("releaseDate"))
                .count());
    }

    @Test
    void errorCreateNegativeDurationFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(-100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("duration"))
                .count());
    }
}