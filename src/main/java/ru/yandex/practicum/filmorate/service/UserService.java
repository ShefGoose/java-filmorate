package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DeleteNotFriendExc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;

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
        checkUserToUpdate(user);
        return userStorage.update(user);
    }

    public User read(int id) {
        if (!userStorage.contains(id)) {
            log.warn(String.format(USER_NOT_FOUND, id));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, id));
        }
        return userStorage.read(id);
    }

    public List<User> readAll() {
        return userStorage.readAll();
    }

    public void addFriend(int userId, int friendId) {
        checkFriendToAdd(userId, friendId);
        boolean status = friendsStorage.contains(friendId, userId);
        friendsStorage.add(userId, friendId, status);
    }

    public void deleteFriend(int userId, int friendId) {
        checkFriendToDelete(userId, friendId);
        friendsStorage.delete(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        if (!userStorage.contains(userId)) {
            log.warn(String.format(USER_NOT_FOUND, userId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, userId));
        }
        List<User> friends = friendsStorage.getFromUserIds(userId).stream()
                .mapToInt(Integer::valueOf)
                .mapToObj(userStorage::read)
                .toList();
        log.info("Возвращен список друзей: {}", friends);
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        checkCommonFriendToGet(userId, otherUserId);
        List<User> commonFriends = CollectionUtils.intersection(
                        friendsStorage.getFromUserIds(userId),
                        friendsStorage.getFromUserIds(otherUserId)).stream()
                .mapToInt(Integer::valueOf)
                .mapToObj(userStorage::read)
                .toList();
        log.info("Возвращён список общих друзей: {}", commonFriends);
        return commonFriends;
    }


    private void checkUserToUpdate(User user) {
        if (!userStorage.contains(user.getId())) {
            log.warn(String.format(USER_NOT_FOUND, user.getId()));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, user.getId()));
        }
    }

    private void checkFriendToAdd(int userId, int friendId) {
        if (!userStorage.contains(userId)) {
            log.warn(String.format(USER_NOT_FOUND, userId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, userId));
        }
        if (!userStorage.contains(friendId)) {
            log.warn(String.format(USER_NOT_FOUND, friendId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, friendId));
        }
        if (userId == friendId) {
            log.warn(String.format(UNABLE_TO_ADD_YOURSELF, userId));
            throw new IllegalArgumentException(String.format(UNABLE_TO_ADD_YOURSELF, userId));
        }
        if (friendsStorage.contains(userId, friendId)) {
            log.warn(String.format(FRIENDSHIP_ALREADY_EXIST, userId, friendId));
            throw new IllegalArgumentException(String.format(FRIENDSHIP_ALREADY_EXIST, userId, friendId));
        }
    }

    private void checkFriendToDelete(int userId, int friendId) {
        if (!userStorage.contains(userId)) {
            log.warn(String.format(USER_NOT_FOUND, userId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, userId));
        }
        if (!userStorage.contains(friendId)) {
            log.warn(String.format(USER_NOT_FOUND, friendId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, friendId));
        }
        if (userId == friendId) {
            log.warn(String.format(UNABLE_TO_DELETE_YOURSELF, userId));
            throw new IllegalArgumentException(String.format(UNABLE_TO_DELETE_YOURSELF, userId));
        }
        if (!friendsStorage.contains(userId, friendId)) {
            log.warn(String.format(FRIENDSHIP_NOT_FOUND, userId, friendId));
            throw new DeleteNotFriendExc(String.format(FRIENDSHIP_NOT_FOUND, userId, friendId));
        }
    }

    private void checkCommonFriendToGet(int userId, int otherUserId) {
        if (!userStorage.contains(userId)) {
            log.warn(String.format(USER_NOT_FOUND, userId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, userId));
        }
        if (!userStorage.contains(otherUserId)) {
            log.warn(String.format(USER_NOT_FOUND, otherUserId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, otherUserId));
        }
        if (userId == otherUserId) {
            log.warn(String.format(UNABLE_FRIENDS_AMONG_THEMSELVES, userId));
            throw new IllegalArgumentException(String.format(UNABLE_FRIENDS_AMONG_THEMSELVES, userId));
        }
    }

}
