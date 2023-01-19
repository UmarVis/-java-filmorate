package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.FilmStorageDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendsDao;
import ru.yandex.practicum.filmorate.storage.user.daoImpl.UserStorageImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserStorageImpl userStorage;
    private final FilmStorageDaoImpl filmStorageDao;
    private final FriendsDao friendsDao;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;

    private final User user = new User(1, "mail@mail.ru", "admin", "Djam",
            LocalDate.of(2020, 1, 1), new HashSet<>());
    private final Film film = new Film(1, "NameFilm", "DescrFilm", LocalDate.of(2020, 1, 1),
            60,5, new HashSet<>(), new LinkedHashSet<>(), new Mpa(1,null));


    @Test
    public void testCrudUserAndFriends(){

        userStorage.create(user);
        User user1 = userStorage.findById(user.getId());
        assertEquals(user, user1);
        assertEquals(user.getId(), 1);
        user.setName("NotTest");
        userStorage.update(user);
        assertEquals(user, userStorage.findById(user.getId()));
        userStorage.deleteUserById(user.getId());
        assertThrows(IdNotFoundException.class, ()-> userStorage.findById(user.getId()));

        userStorage.create(user);
        user.setId(22);
        assertThrows(IdNotFoundException.class, ()-> userStorage.update(user));
        assertThrows(IdNotFoundException.class, ()-> userStorage.findById(55));
        assertThrows(IdNotFoundException.class, ()-> userStorage.deleteUserById(44));

        User friend = new User(1, "friend@mail.ru", "friend", "Dgigi",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        userStorage.create(friend);
        friendsDao.addFriend(2,3);
        Assertions.assertTrue(userStorage.findById(2).getFriendsId().contains(friendsDao.getAllFriends(2).get(0)));
        friendsDao.deleteFriend(2,3);
        assertEquals(0, userStorage.findById(2).getFriendsId().size());

        assertThrows(UserIdException.class, ()-> friendsDao.addFriend(2,33));
        assertThrows(UserIdException.class, ()-> friendsDao.deleteFriend(2,33));
        assertThrows(UserIdException.class, ()-> friendsDao.getAllFriends(33));
    }

    @Test
    public void testCrudFilm() {

        filmStorageDao.create(film);
        assertEquals(film,filmStorageDao.getById(1));
        film.setName("NameTwo");
        filmStorageDao.updateFilm(film);
        assertEquals(film,filmStorageDao.getById(1));

        film.setId(55);
        assertThrows(IdNotFoundException.class, ()-> filmStorageDao.updateFilm(film));
        assertThrows(IdNotFoundException.class, ()-> filmStorageDao.getById(55));
        assertThrows(IdNotFoundException.class,()-> filmStorageDao.getById(66));

        filmStorageDao.deleteById(1);
        assertThrows(IdNotFoundException.class, ()-> filmStorageDao.getById(1));
    }

    @Test
    public void testMpaAndGenre(){

        assertEquals(5, mpaDao.getAll().size());
        assertThrows(IdNotFoundException.class, () -> mpaDao.getId(23));
        assertEquals("G", mpaDao.getId(1).getName());

        assertThrows(IdNotFoundException.class, () -> mpaDao.getId(123));

        assertEquals(6, genreDao.getAll().size());
        assertEquals("Комедия", genreDao.getId(1).getName());
    }

}
