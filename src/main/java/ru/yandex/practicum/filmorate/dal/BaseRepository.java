package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected T findOne(String query, Object... params) {
        List<T> result = jdbc.query(query, mapper, params);
        if (result.isEmpty()) {
            throw new NotFoundException("Не удалось найти данные");
        }
        return result.getFirst();
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowDelete = jdbc.update(query, params);
        return rowDelete > 0;
    }

    protected boolean update(String query, Object... params) {
        int rowUpdate = jdbc.update(query, params);
        return rowUpdate > 0;
    }

    protected Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            return id;
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    protected void batchUpdateBase(String query, BatchPreparedStatementSetter bps) {
        int[] rowUpdate = jdbc.batchUpdate(query, bps);
        if (rowUpdate.length == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        }
    }
}

