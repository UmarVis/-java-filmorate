package ru.yandex.practicum.filmorate.storage.user.daoImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendsDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendsDaoImpl implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (userId == friendId) {
            throw new UserIdException("User and Friend id is same");
        } else if (idCheck(userId) == 0 || idCheck(friendId) == 0) {
            throw new UserIdException("User or Friend id not exist");
        }
        String sqlQuery = "insert into FRIENDS (USER_ID, FRIEND_ID)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        if (idCheck(userId) == 0 || idCheck(friendId) == 0) {
            throw new UserIdException("User or Friend id wrong");
        }
        String sqlQuery = "delete from FRIENDS where USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(Integer userId) {
        String sql = "SELECT u.USER_ID AS id,u.USER_LOGIN,u.USER_NAME,u.USER_EMAIL,u.USER_BIRTHDAY " +
                "FROM FRIENDS AS f " +
                "LEFT JOIN USERS AS u ON u.USER_ID = f.FRIEND_ID " +
                "WHERE f.USER_ID = ?";

        return jdbcTemplate.query(sql, this::rowMapper, userId);
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        String sql = "SELECT  u.* " +
                "FROM FRIENDS AS fs " +
                "JOIN USERS AS u ON fs.FRIEND_ID = u.USER_ID " +
                "WHERE fs.USER_ID = ? AND fs.FRIEND_ID IN (" +
                "SELECT FRIEND_ID FROM FRIENDS WHERE user_id = ?)";

        return jdbcTemplate.query(sql, this::rowMapper, userId, otherId);
    }

    private User rowMapper(ResultSet resultSet, int i) throws SQLException {
        return new User(resultSet.getInt("USER_ID"),
                resultSet.getString("USER_EMAIL"),
                resultSet.getString("USER_LOGIN"),
                resultSet.getString("USER_NAME"),
                resultSet.getDate("USER_BIRTHDAY").toLocalDate()
        );
    }

    private int idCheck(int id) {
        String sql = "select count(*) from USERS where USER_ID = ?";
        int response = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return response;
    }
}
