package chess.domain.piece.factory;

import chess.domain.piece.Color;
import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;
import chess.domain.position.File;
import chess.domain.position.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PawnInitializer implements LocationInitializer {

    private static final List<String> FILES = File.fileSymbols();
    private static final List<String> RANKS_WHITE = Collections.singletonList("2");
    private static final List<String> RANKS_BLACK = Collections.singletonList("7");

    @Override
    public List<Piece> whiteInitialize() {
        List<Piece> pieces = new ArrayList<>();
        for (final String file : FILES) {
            RANKS_WHITE.forEach(
                    rank -> pieces.add(new Pawn(Color.WHITE, new Position(file, rank))));
        }
        return pieces;
    }

    @Override
    public List<Piece> blackInitialize() {
        List<Piece> pieces = new ArrayList<>();
        for (final String file : FILES) {
            RANKS_BLACK.forEach(
                    rank -> pieces.add(new Pawn(Color.BLACK, new Position(file, rank))));
        }
        return pieces;
    }
}
