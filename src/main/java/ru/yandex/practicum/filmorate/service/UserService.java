package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("Получены следущие параметры запроса для добавления в друзья: Id пользователя: {}," +
                        " Id желаемого друга: {}",
                id, friendId);
        if (id.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить самого себя в друзья");
        }
        User wantFriend = userStorage.read(id);
        User newFriend = userStorage.read(friendId);
        if (wantFriend == null) {
            throw new NoSuchElementException("Такого пользователя нет");
        }
        if (newFriend == null) {
            throw new NoSuchElementException("Невозможно добавить в друзья несуществующего пользователя");
        }

        wantFriend.getFriends().add(newFriend.getId());
        newFriend.getFriends().add(wantFriend.getId());
        userStorage.update(wantFriend);
        userStorage.update(newFriend);
        return wantFriend;
    }


    public User deleteFriend(Integer id, Integer friendId) {
        log.info("Получены следущие параметры запроса для удаления из друзей: Id пользователя: {}," +
                        " Id удаляемого друга: {}",
                id, friendId);
        User wantFriend = userStorage.read(id);
        User newFriend = userStorage.read(friendId);
        if (wantFriend == null) {
            throw new NoSuchElementException("Такого пользователя нет");
        }
        if (newFriend == null) {
            throw new NoSuchElementException("Переданный пользователь не состоит в друзьях");
        }

        wantFriend.getFriends().remove(newFriend.getId());
        newFriend.getFriends().remove(wantFriend.getId());
        userStorage.update(wantFriend);
        userStorage.update(newFriend);
        return wantFriend;
    }

    public List<User> getAllFriends(Integer id) {
        User whoGetFriends = userStorage.read(id);
        log.info("Переданный id пользователя для получения списка друзей: {}", id);
        if (whoGetFriends == null) {
            throw new NoSuchElementException("Такого пользователя нет");
        }
        return userStorage.readAll().stream()
                .filter(user -> whoGetFriends.getFriends().contains(user.getId()))
                .toList();
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        log.info("Получены следущие параметры запроса для получения списка общих друзей: Id пользователя: {}," +
                        " Id другого пользователя: {}",
                id, otherId);
        User wantFriend = userStorage.read(id);
        User otherUser = userStorage.read(otherId);
        if (wantFriend == null) {
            throw new NoSuchElementException("Такого пользователя нет");
        }
        if (otherUser == null) {
            throw new NoSuchElementException("Другого пользователя не существует");
        }

        Set<Integer> commonIdFriends = SetUtils.intersection(wantFriend.getFriends(), otherUser.getFriends());
        if (commonIdFriends.isEmpty()) {
            throw new NoSuchElementException("Общих друзей нет");
        }

        return userStorage.readAll().stream()
                .filter(user -> commonIdFriends.contains(user.getId()))
                .toList();
    }
}
