package ru.yandex.practicum.filmorate.storage.user.dao;

import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> get();

    User create(User user);

    User update(User user);

    User findById(Integer id);

    void deleteUserById(int id);

    void deleteAllUsers();
}
