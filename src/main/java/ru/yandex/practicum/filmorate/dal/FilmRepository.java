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
    private static final String QUERY_TOP_FILMS_BY_GENRE = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
            "WHERE GENRE_ID = ? ORDER BY LIKES DESC LIMIT ?";
    private static final String QUERY_TOP_FILMS_BY_YEAR = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID " +
            "WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ? ORDER BY LIKES DESC LIMIT ?";
    private static final String QUERY_TOP_FILMS_BY_GENRE_AND_YEAR = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
            "WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ? AND GENRE_ID = ? ORDER BY LIKES DESC LIMIT ?";
    private static final String QUERY_ALL_GENRES_FILMS = "SELECT * FROM FILMS_GENRES fg, " +
            "GENRES g WHERE fg.GENRE_ID = g.GENRE_ID";
    private static final String QUERY_GENRES_BY_FILM = "SELECT * FROM GENRES g, FILMS_GENRES fg " +
            "WHERE g.GENRE_ID = fg.GENRE_ID AND fg.FILM_ID = ?";
    private static final String QUERY_COMMON_FILMS = "SELECT * FROM films f, MPA_RATINGS m WHERE f.MPA_ID = m.MPA_ID AND f.FILM_ID IN (SELECT m.FILM_ID from " +
            "(SELECT fl.FILM_ID, COUNT(fl.FILM_ID) AS LIKES FROM FILMS_LIKES fl WHERE fl.user_id IN (?, ?) GROUP BY fl.FILM_ID ORDER BY LIKES DESC) AS m WHERE m.LIKES > 1)  ";
    private static final String FIND_BY_DIRECTOR_ID_QUERY = "SELECT f.*, m.MPA_NAME MPA_NAME FROM FILMS f " +
            "LEFT JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID WHERE f.FILM_ID IN " +
            "(SELECT FILM_ID FROM FILMS_DIRECTORS WHERE DIRECTOR_ID = ?)";
    private static final String FIND_ALL_FILM_DIRECTORS_QUERY = "SELECT fd.*, d.DIRECTOR_NAME DIRECTOR_NAME FROM FILMS_DIRECTORS fd " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID";

    private static final String QUERY_SEARCH_FILMS_BY_TITLE = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID WHERE LOWER(f.FILM_NAME) LIKE ? ORDER BY LIKES DESC";

    private static final String QUERY_SEARCH_FILMS_BY_DIRECTOR = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID LEFT JOIN FILMS_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID WHERE LOWER(DIRECTOR_NAME) LIKE ? " +
            "ORDER BY LIKES DESC";

    private static final String QUERY_SEARCH_FILMS_BY_TITLE_AND_DIRECTOR = "SELECT * FROM FILMS f" +
            " LEFT JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES" +
            " FROM FILMS_LIKES GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID LEFT JOIN FILMS_DIRECTORS fd " +
            "ON f.FILM_ID = fd.FILM_ID LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "WHERE LOWER(f.FILM_NAME) LIKE ? OR LOWER(DIRECTOR_NAME) LIKE ? ORDER BY LIKES DESC";

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

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        Collection<Film> films = findMany(QUERY_COMMON_FILMS, userId, friendId);
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

    public List<Film> getFilmsByDirectorId(Integer directorId) {
        return findMany(FIND_BY_DIRECTOR_ID_QUERY, directorId);
    }

    @Override
    public Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        Collection<Film> films;

        if (genreId == null && year == null) {
            films = findMany(QUERY_TOP_FILMS, count);
        } else if (genreId != null && year != null) {
            films = findMany(QUERY_TOP_FILMS_BY_GENRE_AND_YEAR, year, genreId, count);
        } else if (genreId != null) {
            films = findMany(QUERY_TOP_FILMS_BY_GENRE, genreId, count);
        } else {
            films = findMany(QUERY_TOP_FILMS_BY_YEAR, year, count);
        }

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

    public Collection<Film> filmsSearch(String substring, boolean byTitle, boolean byDirector) {
        Collection<Film> films = List.of();
        String stringToFind = "%" + substring.trim().toLowerCase() + "%";

        if (byTitle && byDirector) {
            films = findMany(QUERY_SEARCH_FILMS_BY_TITLE_AND_DIRECTOR, stringToFind, stringToFind);
        } else {
            if (byTitle) {
                films = findMany(QUERY_SEARCH_FILMS_BY_TITLE, stringToFind);
            } else if (byDirector) {
                films = findMany(QUERY_SEARCH_FILMS_BY_DIRECTOR, stringToFind);
            }
        }

        Map<Integer, Set<Genre>> genres = getAllGenres();
        Map<Integer, Set<Director>> directors = getAllDirectors();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), Collections.emptySet()));
            film.setDirectors(directors.getOrDefault(film.getId(), Collections.emptySet()));
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
