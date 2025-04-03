package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.exception.ConstantException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        User needUpdateUser = userStorage.read(user.getId());
        if (user.getEmail() != null) {
            if (user.getEmail().isEmpty()) {
                throw new ValidationException("Email пользователя не должен быть пустым");
            }
            needUpdateUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            if (user.getLogin().isEmpty()) {
                throw new ValidationException("Логин не может быть пустым");
            }
            needUpdateUser.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            if (!user.getName().isEmpty()) {
                needUpdateUser.setName(user.getName());
            } else {
                if (user.getLogin() != null && !user.getLogin().isEmpty()) {
                    needUpdateUser.setName(user.getLogin());
                }
            }
        }

        if (user.getBirthday() != null) {
            needUpdateUser.setBirthday(user.getBirthday());
        }

        return userStorage.update(needUpdateUser);
    }

    public User read(int id) {
        return userStorage.read(id);
    }

    public List<User> readAll() {
        return userStorage.readAll();
    }

    public void addFriend(int userId, int friendId) {
        checkFriendToAdd(userId, friendId);
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        checkFriendToDelete(userId, friendId);
        friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        userStorage.read(userId);
        List<User> friends = friendsStorage.getFriends(userId);
        log.info("Возвращен список друзей: {}", friends);
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        checkCommonFriendToGet(userId, otherUserId);
        List<User> commonFriends = friendsStorage.getCommonFriends(userId, otherUserId);
        log.info("Возвращён список общих друзей: {}", commonFriends);
        return commonFriends;
    }

    private void checkFriendToAdd(int userId, int friendId) {
        userStorage.read(userId);
        userStorage.read(friendId);

        if (userId == friendId) {
            log.warn(String.format(UNABLE_TO_ADD_YOURSELF, userId));
            throw new IllegalArgumentException(String.format(UNABLE_TO_ADD_YOURSELF, userId));
        }
    }

    private void checkFriendToDelete(int userId, int friendId) {
        userStorage.read(userId);
        userStorage.read(friendId);

        if (userId == friendId) {
            log.warn(String.format(UNABLE_TO_DELETE_YOURSELF, userId));
            throw new IllegalArgumentException(String.format(UNABLE_TO_DELETE_YOURSELF, userId));
        }
    }

    private void checkCommonFriendToGet(int userId, int otherUserId) {
        userStorage.read(userId);
        userStorage.read(otherUserId);

        if (userId == otherUserId) {
            log.warn(String.format(UNABLE_FRIENDS_AMONG_THEMSELVES, userId));
            throw new IllegalArgumentException(String.format(UNABLE_FRIENDS_AMONG_THEMSELVES, userId));
        }
    }
}
