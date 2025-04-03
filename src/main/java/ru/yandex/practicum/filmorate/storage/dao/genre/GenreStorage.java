package ru.yandex.practicum.filmorate.storage.dao.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.filmorate.exception.ConstantException.GENRE_NOT_FOUND;

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
       List<Genre> genres = jdbc.query(query, mapper, id);
       if (genres.isEmpty()) {
           log.warn("Не найден жанр ID_{}",id);
           throw new NoSuchElementException(String.format(GENRE_NOT_FOUND, id));
       } else {
           log.info("Возвращён жанр: {}", genres.getFirst());
           return genres.getFirst();
       }
    }

}
