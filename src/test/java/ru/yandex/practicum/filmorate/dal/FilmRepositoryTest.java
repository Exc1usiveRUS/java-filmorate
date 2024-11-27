package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class,
        FilmRowMapper.class})
class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private static Film film1;
    private static Film film2;
    private static Film film3;

    @BeforeAll
    static void beforeAll() {
        film1 = Film.builder()
                .name("Test Film1")
                .description("Test description2")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .genres(Set.of(new Genre(1, "Комедия")))
                .build();

        film2 = Film.builder()
                .name("Test Film2")
                .description("Test description2")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .mpa(new Mpa(2, "PG"))
                .genres(Set.of(new Genre(2, "Драма")))
                .build();

        film3 = Film.builder()
                .name("Test Film3")
                .description("Test description3")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .mpa(new Mpa(3, "PG-13"))
                .genres(Set.of(new Genre(6, "Боевик")))
                .build();
    }

    @Test
    void getFilms() {
        filmRepository.addFilm(film1);
        filmRepository.addFilm(film2);
        filmRepository.addFilm(film3);
        assertThat(filmRepository.getFilms()).isNotEmpty();
    }

    @Test
    void getFilmById() {
        filmRepository.addFilm(film1);
        Film film = filmRepository.getFilmById(film1.getId());
        assertThat(film).hasFieldOrPropertyWithValue("id", 13);
    }

    @Test
    void getTopFilms() {
        filmRepository.addFilm(film1);
        filmRepository.addFilm(film2);
        filmRepository.addFilm(film3);
        assertThat(filmRepository.getTopFilms(10)).isNotEmpty();
    }

    @Test
    void addFilm() {
        filmRepository.addFilm(film1);
        filmRepository.addFilm(film2);
        filmRepository.addFilm(film3);

        assertThat(film2).hasFieldOrPropertyWithValue("id", 5);
    }

    @Test
    void update() {
        filmRepository.addFilm(film1);
        filmRepository.addFilm(film2);
        filmRepository.addFilm(film3);
        Film updatedFilm = Film.builder()
                .id(1)
                .name("Updated Film")
                .description("Updated description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .genres(Set.of(new Genre(1, "Комедия")))
                .likes(Set.of(1, 2))
                .build();

        Film updated = filmRepository.updateFilm(updatedFilm);
        assertThat(updated).hasFieldOrPropertyWithValue("name", "Updated Film");
    }

    @Test
    void delete() {
        filmRepository.addFilm(film1);
        filmRepository.addFilm(film2);
        filmRepository.addFilm(film3);

        filmRepository.deleteFilm(1);
        assertThrows(NotFoundException.class, () -> filmRepository.getFilmById(1));
    }
}