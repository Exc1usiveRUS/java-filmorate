package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTORS (DIRECTOR_NAME) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM DIRECTORS";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String INSERT_FILM_DIRECTORS_QUERY = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) "
            + "VALUES (?, ?) ";
    private static final String DELETE_FILM_DIRECTORS_RELATION_QUERY = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?";
    private static final String DELETE_FILM_DIRECTORS_BY_ID_QUERY = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Director findById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Director create(Director director) {
        Integer id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    public Director update(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    public void deleteDirectorsFromFilm(Integer filmId) {
        delete(DELETE_FILM_DIRECTORS_BY_ID_QUERY, filmId);
    }

    public void deleteAll() {
        deleteAll(DELETE_ALL_QUERY);
    }

    public void deleteFilmDirectors(Film film) {
        jdbc.update(DELETE_FILM_DIRECTORS_RELATION_QUERY, film.getId());
    }

    public void delete(Integer id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    public void addDirectors(Integer filmId, List<Integer> director) {
        jdbc.batchUpdate(INSERT_FILM_DIRECTORS_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, director.get(i));
            }

            @Override
            public int getBatchSize() {
                return director.size();
            }
        });
    }
}
