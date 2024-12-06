package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final String QUERY_FOR_ALL_FILMS = "SELECT * FROM FILMS f, " +
            "MPA_RATINGS m WHERE f.MPA_ID = m.MPA_ID";
    private static final String QUERY_FOR_FILM_BY_ID = "SELECT * FROM FILMS f, MPA_RATINGS m " +
            "WHERE f.MPA_ID = m.MPA_ID AND f.FILM_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILMS " +
            "(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, " +
            "RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String QUERY_TOP_FILMS = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID ORDER BY LIKES DESC LIMIT ?";
    private static final String QUERY_ALL_GENRES_FILMS = "SELECT * FROM FILMS_GENRES fg, " +
            "GENRES g WHERE fg.GENRE_ID = g.GENRE_ID";
    private static final String QUERY_GENRES_BY_FILM = "SELECT * FROM GENRES g, FILMS_GENRES fg " +
            "WHERE g.GENRE_ID = fg.GENRE_ID AND fg.FILM_ID = ?";

    private static final String QUERY_SEARCH_FILMS_BY_TITLES = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID WHERE LOWER(f.FILM_NAME) LIKE ? ORDER BY LIKES DESC";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Film> getFilms() {
        Collection<Film> films = findMany(QUERY_FOR_ALL_FILMS);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), Collections.emptySet()));
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        Film film = findOne(QUERY_FOR_FILM_BY_ID, filmId);
        film.setGenres(getGenresByFilm(filmId));
        return film;
    }

    @Override
    public Collection<Film> getTopFilms(Integer count) {
        Collection<Film> films = findMany(QUERY_TOP_FILMS, count);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        Integer id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    public void deleteFilm(Integer filmId) {
        delete(DELETE_QUERY, filmId);
    }

    public Collection<Film> searchFilmsByTitle(String substring) {
        String title = "%" + substring.trim().toLowerCase() + "%";
        Collection<Film> films = findMany(QUERY_SEARCH_FILMS_BY_TITLES, title);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), Collections.emptySet()));
        }
        return films;
    }

    private Map<Integer, Set<Genre>> getAllGenres() {
        Map<Integer, Set<Genre>> genres = new HashMap<>();
        return jdbc.query(QUERY_ALL_GENRES_FILMS, (ResultSet rs) -> {
            while (rs.next()) {
                Integer filmId = rs.getInt("FILM_ID");
                Integer genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                genres.computeIfAbsent(filmId, k -> new HashSet<>()).add(new Genre(genreId, genreName));
            }
            return genres;
        });
    }

    private Set<Genre> getGenresByFilm(Integer filmId) {
        return jdbc.query(QUERY_GENRES_BY_FILM, (ResultSet rs) -> {
            Set<Genre> genres = new HashSet<>();
            while (rs.next()) {
                Integer genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                genres.add(new Genre(genreId, genreName));
            }
            return genres;
        }, filmId);
    }
}
