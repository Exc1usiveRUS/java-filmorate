package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setName(rs.getString("USERNAME"));
        user.setLogin(rs.getString("LOGIN"));
        user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
        return user;
    }
}