package ru.yandex.practicum.filmorate;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest()
class FilmorateApplicationTests {

    private Validator validator;
    private User user;
    private Film film;

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
        film = new Film();
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
    void shouldValidateFilmSuccess() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldValidateFilmFail() {
        film.setName(null);
        film.setDescription(StringUtils.repeat("d", 201));
        film.setDuration(-20);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Имя фильма не должно быть пустым",
                        "Длина описания превышает 200 символов",
                        "Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void shouldValidateBirthdayFilmFail() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1700,12,1));

        assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    void shouldValidateLoginUserWithSpaceFail() {
        user.setEmail("newuser@gmail.com");
        user.setLogin("New User");
        user.setBirthday(LocalDate.of(1999, 1, 27));

        assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    void shouldUseLoginInsteadNameIfNameIsEmpty() {
        user.setEmail("newuser@gmail.com");
        user.setLogin("NewUser");
        user.setBirthday(LocalDate.of(1999, 1, 27));
        userController.postUser(user);
        assertEquals(user.getName(), user.getLogin());
    }
}
