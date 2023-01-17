package ru.yandex.practicum.filmorate.storage.user.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendsDao;

import java.util.List;

@Component
public class FriendsDaoImpl implements FriendsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (userId == friendId) {
            throw new UserIdException("User and Friend id is same");
        } else if (idCheck(userId) == 0 || idCheck(friendId) == 0) {
            throw  new UserIdException("User or Friend id not exist");
        }
        String sqlQuery = "insert into FRIENDS (USER_ID, FRIEND_ID, FRIEND_STATUS)" +
                "values (?, ?, false)";
        jdbcTemplate.update(sqlQuery,userId, friendId);
        checkOrUpdateFriendStatus(userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        if (idCheck(userId) == 0 || idCheck(friendId) == 0) {
            throw  new UserIdException("User or Friend id wrong");
        }
        String sqlQuery = "delete from FRIENDS where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery,userId, friendId);
    }

    @Override
    public List<Integer> getAllFriends(Integer userId) {
        if (idCheck(userId) == 0) {
            throw  new UserIdException("User ID not found");
        }
        String sqlQuery = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }

    private void checkOrUpdateFriendStatus (long userId, long friendId) {
        String sql = "select COUNT(*) from FRIENDS where USER_ID = ? AND FRIEND_ID = ?";
        int response = jdbcTemplate.queryForObject(sql, Integer.class, friendId, userId);
        if (response == 1) {
            String sql1 = "update FRIENDS set FRIEND_STATUS = true where USER_ID = ? AND FRIEND_ID = ?";
            jdbcTemplate.update(sql1, friendId, userId);
            jdbcTemplate.update(sql1, userId, friendId);
        }
    }

    private int idCheck(int id) {
        String sql = "select count(*) from USERS where USER_ID = ?";
        int response = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return response;
    }
}
