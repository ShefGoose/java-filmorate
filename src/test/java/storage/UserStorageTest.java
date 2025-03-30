package storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserStorageTest {
    private final UserStorage userStorage;
    private User user;
    private User resultUser;

    @BeforeEach
    public void addUser() {
        user = new User();

        user.setName("NewUser");
        user.setEmail("newmail@bk.com");
        user.setLogin("NewLogin");
        user.setBirthday(LocalDate.of(1999,1,1));

        resultUser = userStorage.create(user);
    }

    @Test
    public void getUser() {
        User userTest = userStorage.read(resultUser.getId());

        assertThat(userTest).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(userTest).hasFieldOrPropertyWithValue("email", user.getEmail());
        assertThat(userTest).hasFieldOrPropertyWithValue("login", user.getLogin());
        assertThat(userTest).hasFieldOrPropertyWithValue("birthday",
                user.getBirthday());
    }

    @Test
    public void getAllUsers() {
        List<User> users = userStorage.readAll();

        assertEquals(users.size(), 1);

        assertThat(users.getFirst()).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(users.getFirst()).hasFieldOrPropertyWithValue("email", user.getEmail());
        assertThat(users.getFirst()).hasFieldOrPropertyWithValue("login", user.getLogin());
        assertThat(users.getFirst()).hasFieldOrPropertyWithValue("birthday",
                user.getBirthday());
    }

    @Test
    public void updateUser() {
        User updateUser = new User();
        updateUser.setId(resultUser.getId());
        updateUser.setName("Update name");

        User updateResult = userStorage.update(updateUser);

        assertThat(updateResult).hasFieldOrPropertyWithValue("name", "Update name");
    }
}
