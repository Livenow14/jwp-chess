package chess.dao;

import chess.entity.Movement;

import java.util.List;

public interface MovementDao {

    void save(final Movement movement);

    List<Movement> findByChessName(final String name);
}
