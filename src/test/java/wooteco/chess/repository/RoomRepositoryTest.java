package wooteco.chess.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import wooteco.chess.domain.Board;
import wooteco.chess.domain.piece.Piece;
import wooteco.chess.domain.piece.Team;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class RoomRepositoryTest {
    @Autowired
    private RoomRepository roomRepository;

    @DisplayName("방 저장 확인")
    @Test
    void addRoom() {
        RoomEntity roomEntity = new RoomEntity("방1", Team.WHITE);
        RoomEntity savedRoomEntity = roomRepository.save(roomEntity);
        assertThat(savedRoomEntity).isNotNull();
    }

    @DisplayName("방 이름을 입력하면 방 번호가 반환되는 지 확인")
    @Test
    void findIdByName() {
        RoomEntity roomEntity = new RoomEntity("방1", Team.WHITE);
        roomRepository.save(roomEntity);
        assertThat(roomRepository.findIdByName("방1")).isNotNull();
    }

    @DisplayName("save 테스트")
    @Test
    void save() {
        Set<PieceEntity> pieceEntities = new HashSet<>();
        for (Piece alivePiece : new Board().getPieces().getAlivePieces()) {
            pieceEntities.add(new PieceEntity(alivePiece.getPosition(), alivePiece.toString(), alivePiece.getTeam()));
        }
        assertThat(roomRepository.save(new RoomEntity(UUID.randomUUID(), "방1", Team.WHITE, pieceEntities))).isNotNull();
    }

    @DisplayName("모든 방의 이름이 제대로 반환되는지")
    @Test
    void findAllRoomNamesTest() {
        roomRepository.save(new RoomEntity("방1", Team.WHITE));
        roomRepository.save(new RoomEntity("방2", Team.WHITE));
        assertThat(roomRepository.findAllRoomNames()).contains("방1");
        assertThat(roomRepository.findAllRoomNames()).contains("방2");
    }
}