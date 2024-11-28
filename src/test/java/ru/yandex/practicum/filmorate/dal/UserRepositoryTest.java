package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class,
        UserRowMapper.class,
        FriendshipRepository.class})
class UserRepositoryTest {
    private final UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    private static User user1;
    private static User user2;
    private static User user3;

    @BeforeAll
    static void beforeAll() {
        user1 = User.builder()
                .email("test1@example.com")
                .login("TestUser1")
                .name("name1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        user2 = User.builder()
                .email("test2@example.com")
                .login("TestUser2")
                .name("name2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        user3 = User.builder()
                .email("test3@example.com")
                .login("TestUser3")
                .name("name3")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void getUsers() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        assertThat(userRepository.getUsers()).isNotEmpty();
    }

    @Test
    void getUserById() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        User user = userRepository.getUserById(user1.getId());
        assertThat(user).hasFieldOrPropertyWithValue("id", user.getId());
    }

    @Test
    void createUser() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);

        assertThat(user2).hasFieldOrPropertyWithValue("id", user2.getId());
    }

    @Test
    void update() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        User updatedUser = User.builder()
                .id(1)
                .email("test@example.com")
                .login("UpdateUser")
                .name("Update User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User updated = userRepository.updateUser(updatedUser);
        assertThat(updated).hasFieldOrPropertyWithValue("name", "Update User");
    }

    @Test
    void delete() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);

        userRepository.deleteUser(1);
        assertThrows(NotFoundException.class, () -> userRepository.getUserById(1));
    }

    @Test
    void getCommonFriends() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);

        friendshipRepository.addFriend(user1.getId(), user3.getId());
        friendshipRepository.addFriend(user1.getId(), user2.getId());
        friendshipRepository.addFriend(user2.getId(), user3.getId());
        assertThat(friendshipRepository.getCommonFriends(user1.getId(), user2.getId()))
                .contains(userRepository.getUserById(user3.getId()));
    }

    @Test
    void addFriend() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        friendshipRepository.addFriend(user1.getId(), user3.getId());
        Collection<User> users = friendshipRepository.getAllFriends(user1.getId());
        assertThat(users).contains(userRepository.getUserById(user3.getId()));
        users = friendshipRepository.getAllFriends(user3.getId());
        assertThat(users).doesNotContain(userRepository.getUserById(user1.getId()));
    }

    @Test
    void deleteFriend() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        friendshipRepository.addFriend(user1.getId(), user2.getId());
        userRepository.createUser(user3);
        friendshipRepository.deleteFriend(user1.getId(), user2.getId());
        Collection<User> users = friendshipRepository.getAllFriends(user1.getId());
        assertThat(users).doesNotContain(userRepository.getUserById(user2.getId()));
    }

    @Test
    void getAllFriends() {
        userRepository.createUser(user1);
        userRepository.createUser(user2);
        userRepository.createUser(user3);
        friendshipRepository.addFriend(user1.getId(), user2.getId());
        friendshipRepository.addFriend(user1.getId(), user3.getId());
        Collection<User> users = friendshipRepository.getAllFriends(user1.getId());
        assertThat(users).containsAll(
                List.of(userRepository.getUserById(user2.getId()), userRepository.getUserById(user3.getId()))
        );
    }
}