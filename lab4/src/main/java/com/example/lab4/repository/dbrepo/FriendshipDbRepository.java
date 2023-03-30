package com.example.lab4.repository.dbrepo;




import com.example.lab4.domain.Friendship;
import com.example.lab4.domain.Tuple;
import com.example.lab4.domain.validators.Validator;
import com.example.lab4.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Tuple<Long,Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<Long,Long> id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement
                     ("SELECT * FROM friendships WHERE first_id = ? and second_id = ?")){

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet result = statement.executeQuery();

            result.next();

            Long firstId  = result.getLong("first_id");
            Long secondId = result.getLong("second_id");
            String status = result.getString("status");
            String date = result.getString("date");

            Tuple<Long,Long> foundId = new Tuple<>(firstId,secondId);
            Friendship friendship = new Friendship(foundId,status,date);

            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO if user dosen't exist
    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long firstId = resultSet.getLong("first_id");
                Long secondId = resultSet.getLong("second_id");
                String status = resultSet.getString("status");
                String date = resultSet.getString("date");

                Tuple<Long,Long> id = new Tuple<>(firstId,secondId);
                Friendship friendship = new Friendship(id,status,date);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship friendship) {
        String sql = "insert into friendships (first_id, second_id, status, date) values (?, ?, ?, ?)";
        validator.validate(friendship);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, friendship.getId().getLeft());
            ps.setLong(2, friendship.getId().getRight());
            ps.setString(3, friendship.getStatus());
            ps.setString(4, friendship.getDate());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long,Long> tuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement
                     ("DELETE FROM friendships WHERE first_id = ? and second_id = ?")){
            statement.setLong(1, tuple.getLeft());
            statement.setLong(2, tuple.getRight());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {
        String sql = "UPDATE friendships SET status = ? WHERE first_id = ? AND second_id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, friendship.getStatus());
            ps.setLong(2, friendship.getId().getLeft());
            ps.setLong(3, friendship.getId().getRight());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
