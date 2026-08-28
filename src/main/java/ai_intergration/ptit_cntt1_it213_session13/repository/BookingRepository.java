package ai_intergration.ptit_cntt1_it213_session13.repository;

import ai_intergration.ptit_cntt1_it213_session13.model.Booking;
import ai_intergration.ptit_cntt1_it213_session13.model.constant.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingCode(String bookingCode);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
           "WHERE b.room.id = :roomId " +
           "AND b.status = :status " +
           "AND (b.checkInTime < :checkOutTime AND b.checkOutTime > :checkInTime)")
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkInTime") LocalDateTime checkInTime,
                                     @Param("checkOutTime") LocalDateTime checkOutTime,
                                     @Param("status") BookingStatus status);
}
