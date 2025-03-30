package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public List<Mpa> readAll() {
        String query = "SELECT * FROM MPA";
        List<Mpa> allMpa = jdbc.query(query,mapper);

        log.info("Возвращены все рейтинги MPA: {}", allMpa);

        return allMpa;
    }

    public Mpa read(int id) {
        String query = "SELECT * FROM MPA WHERE id = ?";
        Mpa mpa = jdbc.queryForObject(query, mapper, id);

        log.info("Возвращён рейтинг MPA: {}", mpa);

        return mpa;
    }

    public boolean contains(int id) {
        try {
            read(id);
            log.info("Рейтинг MPA ID_{} найден.", id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Рейтинг MPA ID_{} не найден.", id);
            return false;
        }
    }
}
