package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {
    private static final String QUERY_FOR_ALL_MPA = "SELECT * FROM MPA_RATINGS";
    private static final String QUERY_FOR_BY_ID = "SELECT * FROM MPA_RATINGS WHERE MPA_ID = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Mpa> getAllMpa() {
        return findMany(QUERY_FOR_ALL_MPA);
    }

    public Mpa getMpaById(Integer mpaId) {
        return findOne(QUERY_FOR_BY_ID, mpaId);
    }
}