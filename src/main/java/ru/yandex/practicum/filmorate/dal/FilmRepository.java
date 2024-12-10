package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
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
    private static final String FIND_BY_DIRECTOR_ID_QUERY = "SELECT f.*, m.MPA_NAME MPA_NAME FROM FILMS f " +
            "LEFT JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID WHERE f.FILM_ID IN " +
            "(SELECT FILM_ID FROM FILMS_DIRECTORS WHERE DIRECTOR_ID = ?)";
    private static final String FIND_ALL_FILM_DIRECTORS_QUERY = "SELECT fd.*, d.DIRECTOR_NAME DIRECTOR_NAME FROM FILMS_DIRECTORS fd " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID";

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

    public List<Film> getFilmsByDirectorId(Integer directorId, String sortBy) {
        List<Film> directorFilms = findMany(FIND_BY_DIRECTOR_ID_QUERY, directorId);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        Map<Integer, Set<Director>> directors = getAllDirectors();
        for (Film film : directorFilms) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }
            if (directors.containsKey(film.getId())) {
                film.setDirectors(directors.get(film.getId()));
            }

            if (sortBy.equals("year")) {
                return directorFilms.stream().sorted(Comparator.comparing(Film::getReleaseDate)).toList();
            } else {
                return directorFilms.stream().sorted(Comparator.comparing(Film::getCountLikes).reversed()).toList();
            }
        }
        return directorFilms;
    }

    @Override
    public Collection<Film> getTopFilms(Integer count) {
        Collection<Film> films = findMany(QUERY_TOP_FILMS, count);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        Map<Integer, Set<Director>> directors = getAllDirectors();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }
            if (directors.containsKey(film.getId())) {
                film.setDirectors(directors.get(film.getId()));
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

    private Map<Integer, Set<Director>> getAllDirectors() {
        Map<Integer, Set<Director>> directors = new HashMap<>();
        return jdbc.query(FIND_ALL_FILM_DIRECTORS_QUERY, (ResultSet rs) -> {
            while (rs.next()) {
                Integer filmId = rs.getInt("FILM_ID");
                Integer directorId = rs.getInt("DIRECTOR_ID");
                String directorName = rs.getString("DIRECTOR_NAME");
                directors.computeIfAbsent(filmId, k -> new HashSet<>()).add(new Director(directorId, directorName));
            }
            return directors;
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
