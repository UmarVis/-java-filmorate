package ru.yandex.practicum.filmorate.storage.user.dao;

import java.util.List;

public interface FriendsDao {
    void addFriend(Integer userId, Integer friendId);

    public void deleteFriend(Integer userId, Integer friendId);

    List<Integer> getAllFriends(Integer userId);
}
