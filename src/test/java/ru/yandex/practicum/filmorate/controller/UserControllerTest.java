package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private ValidatorFactory validatorFactory;
    private Validator validator;

    UserController userController;

    @BeforeEach
    public void beforeEach() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        userController = new UserController();
    }

    @AfterEach
    public void afterEach() {
        validatorFactory.close();
    }

    @Test
    public void testCreateUser() {
        userController.createUser(User.builder()
                .email("test@fail.ru")
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void testUpdateUser() {
        User user = User.builder()
                .email("test@fail.ru")
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.createUser(user);
        assertEquals(1, userController.getUsers().size());
        userController.updateUser(User.builder()
                .id(1)
                .name("Петя")
                .build());

        User actualUser = userController.getUsers().stream().toList().getFirst();
        assertEquals(1, userController.getUsers().size());
        assertEquals("Петя", actualUser.getName());
    }

    @Test
    public void errorCreateUserWithEmptyEmail() {
        User user = User.builder()
                .email("")
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email"))
                .count());
    }
    @Test
    public void errorCreateUserWithNullEmail() {
        User user = User.builder()
                .email(null)
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email"))
                .count());
    }

    @Test
    public void errorCreateUserWithInvalidFormatOfEmail() {
        User user = User.builder()
                .email("testmailru@")
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email"))
                .count());
    }

    @Test
    public void errorCreateUserWithBlankEmail() {
        User user = User.builder()
                .email(" ")
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email"))
                .count());
    }

    @Test
    public void errorCreateUserWithEmptyLogin() {
        User user = User.builder()
                .email("test@fail.ru")
                .login("")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    public void errorCreateUserWithNullLogin() {
        User user = User.builder()
                .email("test@fail.ru")
                .login(null)
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    public void errorCreateUserWithBlankLogin() {
        User user = User.builder()
                .email("test@fail.ru")
                .login(" ")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    public void errorCreateUserWithSpaceInLogin() {
        User user = User.builder()
                .email("test@fail.ru")
                .login("Test login")
                .name("Вася")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    public void errorCreateUserWithInvalidBirthday() {
        User user = User.builder()
                .email("test@fail.ru")
                .login("testLogin")
                .name("Вася")
                .birthday(LocalDate.now().plusYears(1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("birthday"))
                .count());
    }
}
