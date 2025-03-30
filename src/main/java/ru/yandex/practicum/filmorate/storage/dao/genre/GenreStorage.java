package ru.yandex.practicum.filmorate.storage.dao.genre;



import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.List;

@Repository
@Slf4j
@AllArgsConstructor
public class GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public List<Genre> readAll() {
        String query = "SELECT * FROM genres";
        List<Genre> allGenres = jdbc.query(query,mapper);

        log.info("Возвращены все жанры: {}", allGenres);

        return allGenres;
    }

    public Genre read(int id) {
        String query = "SELECT * FROM genres WHERE id = ?";
       Genre genre = jdbc.queryForObject(query, mapper, id);

        log.info("Возвращён жанр: {}", genre);

       return genre;
    }

    public boolean contains(int id) {
        try {
            read(id);
            log.info("Найден жанр ID_{}",id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не найден жанр ID_{}",id);
            return false;
        }
    }
}
