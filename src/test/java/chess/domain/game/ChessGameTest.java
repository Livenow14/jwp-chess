package chess.domain.game;

import chess.domain.OutputFactory;
import chess.domain.position.Position;
import chess.exception.InvalidMoveException;
import chess.exception.ValidTurnException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChessGameTest {

    @Test
    @DisplayName("움직임 확인")
    void move() {
        ChessGame chessGame = ChessGame.newGame();

        OutputFactory.printCurrentBoard(chessGame.chessBoard().chessBoard());

        chessGame.moveByTurn(new Position("a2"), new Position("a4"));
        chessGame.moveByTurn(new Position("b7"), new Position("b5"));
        chessGame.moveByTurn(new Position("a4"), new Position("b5"));

        OutputFactory.printCurrentBoard(chessGame.chessBoard().chessBoard());
    }

    @Test
    @DisplayName("움직이고 또 움직이면 에러가 발생")
    void moveException() {
        ChessGame chessGame = ChessGame.newGame();

        OutputFactory.printCurrentBoard(chessGame.chessBoard().chessBoard());

        chessGame.moveByTurn(new Position("a2"), new Position("a4"));

        OutputFactory.printCurrentBoard(chessGame.chessBoard().chessBoard());

        assertThatThrownBy(() -> chessGame.moveByTurn(new Position("a4"), new Position("a5")))
            .isInstanceOf(ValidTurnException.class);
    }

    @Test
    @DisplayName("폰 이동 전략에 맞지 않는 곳으로 움직일 때 에러 확인")
    void pawnMoveStrategyException() {
        ChessGame chessGame = ChessGame.newGame();
        assertThatThrownBy(() -> chessGame.moveByTurn(new Position("a2"), new Position("a5")))
            .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    @DisplayName("룩 이동 전략에 맞지 않는 곳으로 움직일 때 에러 확인")
    void rookMoveStrategyException() {
        ChessGame chessGame = ChessGame.newGame();
        assertThatThrownBy(() -> chessGame.moveByTurn(new Position("a1"), new Position("a3")))
            .isInstanceOf(InvalidMoveException.class);
    }
}