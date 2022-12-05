package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidateTest {

    @Test
    public void createdFilmTest() {
        final FilmController film = new FilmController();
        Film fm = new Film(1, "Я Легенда", "Фильм про зомби", LocalDate.of(2010, 12,
                24), 90);
        film.create(fm);
        assertEquals(1, film.getFilms().size(), "Фильм не создан");
    }

    @Test
    public void notNameFilmTest() {
        final FilmController film = new FilmController();
        Film fm = new Film(1, "", "Фильм про зомби", LocalDate.of(2010, 12, 24),
                90);
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        film.create(fm);
                    }
                });
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    public void maxSimFilmTest() {
        final FilmController film = new FilmController();
        Film fm = new Film(1, "Я Легенда",
                "Фильм про зомбииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииии" +
                        "ииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииии" +
                        "иииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииии",
                LocalDate.of(2010, 12, 24), 90);
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        film.create(fm);
                    }
                });
        assertEquals("Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    public void dataFilmTest() {
        final FilmController film = new FilmController();
        Film fm = new Film(1, "Я Легенда", "Фильм про зомби", LocalDate.of(1800, 12, 24),
                90);
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        film.create(fm);
                    }
                });
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    public void durationFilmTest() {
        final FilmController film = new FilmController();
        Film fm = new Film(1, "Я Легенда", "Фильм про зомби", LocalDate.of(2010, 12, 24),
                -1);
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        film.create(fm);
                    }
                });
        assertEquals("Продолжительность фильма должна быть положительной", ex.getMessage());
    }

    @Test
    public void createdUserTest() {
        UserController uc = new UserController();
        User user = new User(1, "maks@yandex.ru", "maks_1992", "Maksim",
                LocalDate.of(1992, 12, 24));
        uc.newUser(user);
        assertEquals(1, uc.users().size(), "Пользователь не создан");
    }

    @Test
    public void mailUserTest() {
        UserController uc = new UserController();
        User user = new User(1, "maksyandex.ru", "maks_1992", "Maksim",
                LocalDate.of(1992, 12, 24));
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        uc.newUser(user);
                    }
                });
        assertEquals("Электронная почта не может быть пустой или должна содержать символ @",
                ex.getMessage());
    }

    @Test
    public void loginUserTest() {
        UserController uc = new UserController();
        User user = new User(1, "maks@yandex.ru", "", "Maksim",
                LocalDate.of(1992, 12, 24));
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        uc.newUser(user);
                    }
                });
        assertEquals("Логин не может быть пустым и содержать пробелы",
                ex.getMessage());
    }

    @Test
    public void bdUserTest() {
        UserController uc = new UserController();
        User user = new User(1, "maks@yandex.ru", "maks_1992", "Maksim",
                LocalDate.of(2025, 12, 24));
        final ValidationException ex = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        uc.newUser(user);
                    }
                });
        assertEquals("Дата рождения не может быть в будущем",
                ex.getMessage());
    }

    @Test
    public void createdUserNotNameTest() {
        UserController uc = new UserController();
        User user = new User(1, "maks@yandex.ru", "maks_1992", "",
                LocalDate.of(1992, 12, 24));
        uc.newUser(user);
        assertEquals("maks_1992", user.getName(), "Не назначено имя");
    }
}

