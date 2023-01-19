package ru.yandex.practicum.filmorate.storage.user.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> get();

    User create(User user);

    User update(User user);

    User findById(Integer id);

    void deleteUserById(int id);

    void deleteAllUsers();

    List<User> getMutualFriends(Integer userId, Integer otherId);

    List<User> getAllFriends(Integer userId);
}
