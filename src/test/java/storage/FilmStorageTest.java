package storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmStorageTest {
    private final FilmStorage filmStorage;
    private Film film;
    private Film resultFilm;
    private Mpa mpa;
    private Genre genre;

    @BeforeEach
    public void addFilm() {
        film = new Film();
        mpa = new Mpa();
        genre = new Genre();

        mpa.setId(1);
        genre.setId(1);

        film.setName("NewFilm");
        film.setDescription("About everything");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1999,1,1));
        film.setMpa(mpa);
        film.setGenres(Set.of(genre));

        resultFilm = filmStorage.create(film);
    }

    @Test
    public void getFilm() {
        Film filmTest = filmStorage.read(resultFilm.getId());

        assertThat(filmTest).hasFieldOrPropertyWithValue("name", film.getName());
        assertThat(filmTest).hasFieldOrPropertyWithValue("description", film.getDescription());
        assertThat(filmTest).hasFieldOrPropertyWithValue("duration", 100);
        assertThat(filmTest).hasFieldOrPropertyWithValue("releaseDate",
                film.getReleaseDate());
    }

    @Test
    public void getAllFilms() {
        List<Film> films = filmStorage.readAll();

        assertEquals(films.size(), 1);

        assertThat(films.getFirst()).hasFieldOrPropertyWithValue("name", film.getName());
        assertThat(films.getFirst()).hasFieldOrPropertyWithValue("description", film.getDescription());
        assertThat(films.getFirst()).hasFieldOrPropertyWithValue("duration", 100);
        assertThat(films.getFirst()).hasFieldOrPropertyWithValue("releaseDate",
                film.getReleaseDate());
    }

    @Test
    public void updateFilm() {
        Film updateFilm = new Film();
        Mpa updateMpa = new Mpa();
        updateMpa.setId(2);

        updateFilm.setId(resultFilm.getId());
        updateFilm.setName("Update name");
        updateFilm.setDescription("Update desc");
        updateFilm.setDuration(150);
        updateFilm.setReleaseDate(LocalDate.of(1999,1,2));
        updateFilm.setMpa(updateMpa);


        Film updateResult = filmStorage.update(updateFilm);

        assertThat(updateResult).hasFieldOrPropertyWithValue("name", "Update name");
        assertThat(updateResult).hasFieldOrPropertyWithValue("description", "Update desc");
        assertThat(updateResult).hasFieldOrPropertyWithValue("duration", 150);
        assertThat(updateResult).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(1999,1,2));
        assertThat(updateResult).hasFieldOrPropertyWithValue("mpa", updateMpa);
    }
}
