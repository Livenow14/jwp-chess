package chess.service;

import chess.dao.ChessDao;
import chess.dao.MovementDao;
import chess.domain.board.Board;
import chess.domain.game.ChessGame;
import chess.domain.position.Position;
import chess.entity.Chess;
import chess.entity.Movement;
import chess.service.dto.ChessSaveRequestDto;
import chess.service.dto.CommonResponseDto;
import chess.service.dto.GameStatusDto;
import chess.service.dto.GameStatusRequestDto;
import chess.service.dto.MoveRequestDto;
import chess.service.dto.MoveResponseDto;
import chess.service.dto.ResponseCode;
import chess.service.dto.TilesDto;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpringChessService {

    private final ChessDao chessDao;
    private final MovementDao movementDao;

    public SpringChessService(final ChessDao chessDao, final MovementDao movementDao) {
        this.chessDao = chessDao;
        this.movementDao = movementDao;
    }

    public TilesDto emptyBoard() {
        return new TilesDto(Board.emptyBoard());
    }

    @Transactional
    public CommonResponseDto<MoveResponseDto> movePiece(final MoveRequestDto requestDto) {
        ChessGame chessGame = ChessGame.newGame();
        Chess chess = movedChess(chessGame, requestDto.getChessName());
        chessGame.moveByTurn(new Position(requestDto.getSource()), new Position(requestDto.getTarget()));
        movementDao.save(new Movement(chess.getId(), requestDto.getSource(), requestDto.getTarget()));

        if (chessGame.isGameOver()) {
            chess.changeRunning(!chessGame.isGameOver());
            chess.changeWinnerColor(chessGame.findWinner());
            chessDao.update(chess);
        }

        return new CommonResponseDto<>(
            new MoveResponseDto(requestDto.getSource(), requestDto.getTarget(), chessGame.calculateScore(),
                !chess.isRunning()),
            ResponseCode.OK.code(), ResponseCode.OK.message());
    }

    @Transactional
    public CommonResponseDto<GameStatusDto> saveChess(final ChessSaveRequestDto requestDto) {
        Chess chess = new Chess(requestDto.getName());

        if (chessDao.findByName(requestDto.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 방 이름입니다.");
        }

        chessDao.save(chess);
        return new CommonResponseDto<>(ResponseCode.OK.code(), ResponseCode.OK.message());
    }

    @Transactional
    public void changeGameStatus(final GameStatusRequestDto requestDto) {
        ChessGame chessGame = ChessGame.newGame();
        Chess chess = movedChess(chessGame, requestDto.getChessName());

        chess.changeRunning(!requestDto.isGameOver());
        chess.changeWinnerColor(chessGame.findWinner());
        chessDao.update(chess);
    }

    public CommonResponseDto<GameStatusDto> loadChess(final String name) {
        ChessGame chessGame = ChessGame.newGame();
        Chess chess = movedChess(chessGame, name);

        return new CommonResponseDto<>(new GameStatusDto(chessGame.pieces(),
            chessGame.calculateScore(), !chess.isRunning(), chess.getWinnerColor()),
            ResponseCode.OK.code(), ResponseCode.OK.message());
    }

    private Chess movedChess(final ChessGame chessGame, final String name) {
        Chess chess = findChessByName(name);
        List<Movement> movements = movementDao.findByChessName(chess.getName());

        for (Movement movement : movements) {
            chessGame.moveByTurn(new Position(movement.getSourcePosition()), new Position(movement.getTargetPosition()));
        }
        return chess;
    }

    private Chess findChessByName(final String chessName) {
        return chessDao.findByName(chessName).orElseThrow(() -> new IllegalArgumentException("없는 게임 이름입니다"));
    }
}