package ai_intergration.ptit_cntt1_it213_session13.repository;

import ai_intergration.ptit_cntt1_it213_session13.model.Room;
import ai_intergration.ptit_cntt1_it213_session13.model.constant.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(RoomStatus status);
    Optional<Room> findByRoomNumber(String roomNumber);
}
