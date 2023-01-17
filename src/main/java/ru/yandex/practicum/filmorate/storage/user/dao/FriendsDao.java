package ru.yandex.practicum.filmorate.storage.user.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsDao {
    void addFriend(Integer userId, Integer friendId);
    public void deleteFriend(Integer userId, Integer friendId);
    List<Integer> getAllFriends(Integer userId);
}
