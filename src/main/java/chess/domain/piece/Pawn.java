package chess.domain.piece;

import chess.domain.piece.direction.MoveStrategies;
import chess.domain.piece.direction.MoveStrategy;
import chess.domain.piece.direction.Nothing;
import chess.domain.position.*;

import java.util.Arrays;
import java.util.List;

public final class Pawn extends Piece {

    private static final String INITIAL_NAME = "P";
    private static final List<Integer> INITIAL_RANKS = Arrays.asList(2, 7);
    private static final double SCORE = 1;

    private final MoveStrategies moveStrategies;

    public Pawn(final Color color, final Position position) {
        super(color, INITIAL_NAME, position);
        this.moveStrategies = MoveStrategies.pawnMoveStrategies();
    }

    @Override
    public Piece move(final Target target) {
        return new Pawn(this.color(), target.piece().position());
    }

    @Override
    public boolean canMove(final Target target) {
        final List<Integer> result = subtractByTeam(target);
        final MoveStrategy moveStrategy = moveStrategies.strategies().stream()
                .filter(strategy -> strategy.isSameDirection(result.get(0), result.get(1)))
                .findAny()
                .orElse(new Nothing());

        return !moveStrategy.isNothing() && checkPossible(moveStrategy, target, position().rank());
    }

    @Override
    public double score(final List<Piece> pieces) {
        File currentFile = this.position().file();
        boolean isSameFile = pieces.stream()
                .filter(Piece::isPawn)
                .filter(piece -> !this.equals(piece))
                .anyMatch(piece -> currentFile == piece.position().file());
        if (isSameFile) {
            return SCORE / 2;
        }
        return SCORE;
    }

    private List<Integer> subtractByTeam(final Target target) {
        final Source source = new Source(this);
        if (source.isBlack()) {
            return source.subtract(target);
        }
        return target.subtract(source);
    }

    private boolean checkPossible(final MoveStrategy moveStrategy, final Target target, final Rank rank) {
        if (moveStrategy.isNorth()) {
            return target.isBlank();
        }
        if (moveStrategy.isNorthWest() || moveStrategy.isNorthEast()) {
            return !target.isBlank() && target.isOpponent(new Source(this));
        }
        if (moveStrategy.isInitialPawnNorth()) {
            return INITIAL_RANKS.contains(rank.value()) && target.isBlank();
        }
        return false;
    }

    @Override
    public boolean isPawn() {
        return true;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public boolean canMultiMove() {
        return true;
    }
}
