package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> readAll();

    User create(User user);

    User update(User user);

    void delete(int id);

    User read(int id);
}
