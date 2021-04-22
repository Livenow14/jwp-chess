package chess.dao;

import chess.domain.piece.Color;
import chess.entity.Chess;
import chess.exception.DuplicateRoomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class DBChessDao implements ChessDao {

    private final ConnectionPool connectionPool;
    private final Logger logger = LoggerFactory.getLogger(DBChessDao.class);

    public DBChessDao(final ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;

        String query = "CREATE TABLE IF NOT EXISTS chess ( " +
                "chess_id VARCHAR(36) NOT NULL," +
                "name VARCHAR(64) NOT NULL," +
                "winner_color VARCHAR(64) NOT NULL," +
                "is_running boolean not null default false ," +
                "created_date TIMESTAMP(6)," +
                "PRIMARY KEY (chess_id)" +
                ");";

        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();

            closeResources(connection, preparedStatement);

        } catch (SQLException e) {
            logger.info("테이블 생성오류" + e.getMessage());
        }
    }

    @Override
    public void save(final Chess chess) {
        String query = "INSERT INTO chess VALUES (?, ?, ?, ?, ?)";

        if (findByName(chess.getName()).isPresent()) {
            throw new DuplicateRoomException();
        }

        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chess.getId());
            preparedStatement.setString(2, chess.getName());
            preparedStatement.setString(3, chess.getWinnerColor().getName());
            preparedStatement.setBoolean(4, chess.isRunning());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(chess.getCreatedDate()));

            preparedStatement.executeUpdate();
            closeResources(connection, preparedStatement);
        } catch (SQLException e) {
            logger.info("chess 저장 오류" + e.getMessage());
        }
    }

    @Override
    public Optional<Chess> findByName(final String name) {
        String query = "SELECT * FROM chess" +
                " WHERE name = ?" +
                " ORDER BY created_date";

        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                closeResources(connection, preparedStatement);
                resultSet.close();
                return Optional.empty();
            }
            Chess chess = new Chess(
                    resultSet.getString("chess_id"),
                    resultSet.getString("name"),
                    Color.findByValue(resultSet.getString("winner_color")),
                    resultSet.getBoolean("is_running"),
                    resultSet.getTimestamp("created_date").toLocalDateTime()
            );

            closeResources(connection, preparedStatement);
            resultSet.close();
            return Optional.ofNullable(chess);
        } catch (SQLException e) {
            logger.info("chess 저장 오류" + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void update(final Chess chess) {
        String query = "UPDATE chess" +
                " SET winner_color = ?, is_running = ?" +
                " WHERE chess_id = ?";

        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, chess.getWinnerColor().toString());
            preparedStatement.setBoolean(2, chess.isRunning());
            preparedStatement.setString(3, chess.getId());
            preparedStatement.executeUpdate();

            closeResources(connection, preparedStatement);
        } catch (SQLException e) {
            logger.info("chess 업데이트 오류" + e.getMessage());
        }
    }

    @Override
    public void deleteByName(final String name) {
        String query = "DELETE FROM chess WHERE name = ?";

        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            closeResources(connection, preparedStatement);
        } catch (SQLException e) {
            logger.info("chess 삭제 오류" + e.getMessage());
        }
    }

    private void closeResources(Connection connection, PreparedStatement preparedStatement) throws SQLException {
        connectionPool.releaseConnection(connection);
        preparedStatement.close();
    }
}
