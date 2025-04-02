package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.advice.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@AllArgsConstructor
public class FriendsStorage {
    private final JdbcTemplate jdbc;

    public void addFriend(int userId, int friendId) {
        String query = "INSERT INTO friends (user_id, friend_id) VALUES(?, ?)";
        FriendshipStatus currentStatus = checkFriendship(userId, friendId);

        switch (currentStatus) {
            case NOT_FRIENDS,
                 ONE_WAY_TO:
                jdbc.update(query, userId, friendId);
                break;
            case ONE_WAY_FROM:
                throw new IllegalStateException("Запрос в друзья уже отправлен");
            case MUTUAL:
                throw new IllegalStateException("Пользователи уже друзья");
        }
    }

    public void deleteFriend(int userId, int friendId) {
        String deleteQuery = "DELETE FROM friends "
                + "WHERE user_id = ? "
                + "AND friend_id = ?";

        int resultDelete = jdbc.update(deleteQuery, userId, friendId);

        if (resultDelete == 0) {
            log.warn("У пользователя ID_{} нет в друзьях пользователя ID_{}", userId, friendId);
        } else {
            log.info("Пользователь ID_{} удалил из друзей пользователя ID_{}", userId, friendId);
        }
    }

    public List<User> getFriends(int userId) {
        String query = "SELECT f.friend_id, u.* "
                + "FROM friends f "
                + "JOIN users u ON f.friend_id = u.id "
                + "WHERE f.user_id = ?";

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(query, userId);

            return rows.stream()
                    .map(row -> {
                        User user = new User();
                        user.setId(((Number) row.get("id")).intValue());
                        user.setName((String) row.get("name"));
                        user.setEmail((String) row.get("email"));
                        user.setLogin((String) row.get("login"));
                        user.setBirthday(((Date) row.get("birthday")).toLocalDate());
                        return user;
                    })
                    .toList();
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        String query = "SELECT f1.friend_id, u.* "
                + "FROM friends f1 "
                + "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                "JOIN users u ON f1.friend_id = u.id "
                + "WHERE f1.user_id = ? "
                + "AND f2.user_id = ?";

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(query, userId, otherUserId);

            return rows.stream()
                    .map(row -> {
                        User user = new User();
                        user.setId(((Number) row.get("id")).intValue());
                        user.setName((String) row.get("name"));
                        user.setEmail((String) row.get("email"));
                        user.setLogin((String) row.get("login"));
                        user.setBirthday(((Date) row.get("birthday")).toLocalDate());
                        return user;
                    })
                    .toList();
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    //Взаимная дружба определяется наличием парных записей
    public FriendshipStatus checkFriendship(int userId, int friendId) {
        String query = "SELECT " +
                "EXISTS(SELECT 1 FROM friends WHERE user_id = ? AND friend_id = ?) AS status_from, "
                + "EXISTS(SELECT 1 FROM friends WHERE user_id = ? AND friend_id = ?) AS status_to";
        try {
            Map<String, Object> result = jdbc.queryForMap(query, userId, friendId, friendId, userId);


            boolean statusFrom = (Boolean) result.get("status_from");
            boolean statusTo = (Boolean) result.get("status_to");

            if (statusFrom && statusTo) {
                return FriendshipStatus.MUTUAL;
            } else if (statusFrom) {
                return FriendshipStatus.ONE_WAY_FROM;
            } else if (statusTo) {
                return FriendshipStatus.ONE_WAY_TO;
            }
            return FriendshipStatus.NOT_FRIENDS;
        } catch (EmptyResultDataAccessException e) {
            return FriendshipStatus.NOT_FRIENDS;
        }
    }
}
