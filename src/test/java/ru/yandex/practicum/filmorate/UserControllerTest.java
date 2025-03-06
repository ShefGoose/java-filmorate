package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class UserControllerTest {
    private Validator validator;
    private User user;

    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
    }

    @Test
    void shouldValidateUserSuccess() {
        user.setEmail("newuser@gmail.com");
        user.setLogin("NewUser");
        user.setBirthday(LocalDate.of(1999, 1, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldValidateUserFail() {
        user.setEmail("newuser@");
        user.setLogin(null);
        user.setBirthday(LocalDate.of(2999, 1, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Неверный формат Email",
                        "Логин не может быть пустым",
                        "Дата рождения не может быть в будущем");
    }

    @Test
    void shouldValidateEmptyEmailFail() {
        user.setEmail(null);
        user.setLogin("NewUser");
        user.setBirthday(LocalDate.of(1999, 1, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Почта не может быть пустой");
    }

    @Test
    void shouldValidateLoginUserWithSpaceFail() {
        user.setEmail("newuser@gmail.com");
        user.setLogin("New User");
        user.setBirthday(LocalDate.of(1999, 1, 27));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Логин не может содержать пробелы");
    }

    @Test
    void shouldUseLoginInsteadNameIfNameIsEmpty() {
        user.setEmail("newuser@gmail.com");
        user.setLogin("NewUser");
        user.setBirthday(LocalDate.of(1999, 1, 27));
        userController.postUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void shouldValidateNullReleaseDateFail() {
        user.setEmail("newuser@gmail.com");
        user.setLogin("NewUser");
        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата рождения не может быть null");
    }
}
