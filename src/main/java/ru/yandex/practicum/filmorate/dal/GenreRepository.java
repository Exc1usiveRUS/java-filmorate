package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String QUERY_FOR_ALL_GENRES = "SELECT * FROM GENRES";
    private static final String INSERT_QUERY = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String QUERY_FOR_GENRE_BY_ID = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String DELETE_ALL_FROM_FILM_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getAllGenres() {
        return findMany(QUERY_FOR_ALL_GENRES);
    }

    public Genre getGenreById(Integer id) {
        return findOne(QUERY_FOR_GENRE_BY_ID, id);
    }

    public void addGenres(Integer filmId, List<Integer> genresIds) {
        batchUpdateBase(INSERT_QUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresIds.size();
            }
        });
    }

    public void deleteGenres(Integer filmId) {
        update(DELETE_ALL_FROM_FILM_QUERY, filmId);
    }
}