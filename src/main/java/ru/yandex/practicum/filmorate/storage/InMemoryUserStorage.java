package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ValidationException("Id пользователя должно быть указано");
        }
        if (!users.containsKey(updatedUser.getId())) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User user = users.get(updatedUser.getId());
        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()
                && updatedUser.getEmail().matches("^[\\w-.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$")) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getLogin() != null && !updatedUser.getLogin().isBlank()) {
            user.setLogin(updatedUser.getLogin());
        }
        if (updatedUser.getBirthday() != null) {
            user.setBirthday(updatedUser.getBirthday());
        }
        users.put(updatedUser.getId(), updatedUser);
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        return users.get(id);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    private int getNextId() {
        return nextId++;
    }
}
