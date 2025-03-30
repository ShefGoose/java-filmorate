package ru.yandex.practicum.filmorate.storage.dao.friends;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.storage.mapper.FriendsRowMapper;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@AllArgsConstructor
public class FriendsStorage {
    private final JdbcTemplate jdbc;
    private final FriendsRowMapper friendsMapper;

    public void add(int userId, int friendId, boolean status) {
        String query = "INSERT INTO friends (user_id, friend_id, status) VALUES(?, ?, ?)";
        jdbc.update(query, userId, friendId, status);
        Friends newFriends = get(userId, friendId);
        log.info("Добавлена связь между пользователями ID_{} и ID_{}: {}", userId, friendId, newFriends);
    }

    public void delete(int userId, int friendId) {
        Friends newFriends = Objects.requireNonNull(get(userId, friendId));
        String deleteQuery = "DELETE FROM friends "
                + "WHERE user_id = ? "
                + "AND friend_id = ?";
        String updateQuery = "UPDATE friends "
                + "SET status = false "
                + "WHERE user_id = ?"
                + "AND friend_id = ?";
        jdbc.update(deleteQuery, userId, friendId);

        if (newFriends.isStatus()) {
            jdbc.update(updateQuery, userId, friendId);
        }
        log.info("Удалена связь между пользователями ID_{} и ID_{}: {}", userId, friendId, newFriends);
    }

    public List<Integer> getFromUserIds(int userId) {
        String query = "SELECT * "
                + "FROM friends "
                + "WHERE user_id = ? ";
        List<Integer> friends = jdbc.query(query, friendsMapper, userId)
                .stream()
                .map(Friends::getUserid)
                .toList();
        log.info("Возвращены запросы на дружбу с пользователем ID_{}: {}", userId, friends);
        return friends;
    }

    private Friends get(int userId, int friendId) {
        String query = "SELECT * FROM friends " +
                "WHERE user_id = ? " +
                "AND friend_id = ?";
        Friends newFriends = jdbc.queryForObject(query,friendsMapper, userId, friendId);
        log.info("Возвращена связь между пользователями ID_{} и ID_{}: {}", userId, friendId, newFriends);
        return newFriends;
    }

    public boolean contains(int userId, int friendId) {
        try {
            get(userId, friendId);
            log.info("Найден запрос на дружбу от пользователя ID_{} к пользователю ID_{}",
                    userId, friendId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не найден запрос на дружбу от пользователя ID_{} к пользователю ID_{}",
                    userId, friendId);
            return false;
        }
    }
}
