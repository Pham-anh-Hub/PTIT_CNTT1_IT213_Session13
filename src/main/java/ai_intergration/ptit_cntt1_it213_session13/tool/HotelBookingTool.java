package ai_intergration.ptit_cntt1_it213_session13.tool;

import ai_intergration.ptit_cntt1_it213_session13.model.Booking;
import ai_intergration.ptit_cntt1_it213_session13.model.constant.BookingStatus;
import ai_intergration.ptit_cntt1_it213_session13.model.Room;
import ai_intergration.ptit_cntt1_it213_session13.model.constant.RoomStatus;
import ai_intergration.ptit_cntt1_it213_session13.repository.BookingRepository;
import ai_intergration.ptit_cntt1_it213_session13.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelBookingTool {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public record BookingRequest(
            String customerName,
            String roomNumber,
            String checkInTime,
            String checkOutTime
    ) {}

    public record BookingResponse(
            boolean success,
            String bookingCode,
            String customerName,
            String roomNumber,
            String roomType,
            String checkInTime,
            String checkOutTime,
            Double pricePerNight,
            String message
    ) {}

    @Tool(description = "Thực hiện đặt phòng khách sạn QuickStay Hotel khi khách hàng cung cấp tên, số phòng và thời gian checkIn, checkOut (định dạng 'yyyy-MM-dd HH:mm').")
    public BookingResponse bookRoom(BookingRequest request) {
        log.info("HotelBookingTool.bookRoom được gọi với request: {}", request);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime checkIn = LocalDateTime.parse(request.checkInTime(), formatter);
            LocalDateTime checkOut = LocalDateTime.parse(request.checkOutTime(), formatter);

            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                return new BookingResponse(false, null, request.customerName(), request.roomNumber(), null,
                        request.checkInTime(), request.checkOutTime(), 0.0,
                        "Thời gian trả phòng (checkOut) phải sau thời gian nhận phòng (checkIn).");
            }

            Optional<Room> roomOpt = roomRepository.findByRoomNumber(request.roomNumber());
            if (roomOpt.isEmpty()) {
                return new BookingResponse(false, null, request.customerName(), request.roomNumber(), null,
                        request.checkInTime(), request.checkOutTime(), 0.0,
                        "Không tìm thấy phòng số " + request.roomNumber() + " trong hệ thống.");
            }

            Room room = roomOpt.get();
            if (room.getStatus() == RoomStatus.MAINTENANCE) {
                return new BookingResponse(false, null, request.customerName(), request.roomNumber(), room.getRoomType(),
                        request.checkInTime(), request.checkOutTime(), 0.0,
                        "Phòng số " + request.roomNumber() + " hiện đang bảo trì.");
            }

            boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                    room.getId(), checkIn, checkOut, BookingStatus.CONFIRMED
            );

            if (isOverlapping) {
                return new BookingResponse(false, null, request.customerName(), request.roomNumber(), room.getRoomType(),
                        request.checkInTime(), request.checkOutTime(), 0.0,
                        "Phòng " + request.roomNumber() + " đã có khách đặt trong khoảng thời gian từ " + request.checkInTime() + " đến " + request.checkOutTime() + ".");
            }

            String bookingCode = "QS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Booking booking = Booking.builder()
                    .bookingCode(bookingCode)
                    .customerName(request.customerName())
                    .room(room)
                    .checkInTime(checkIn)
                    .checkOutTime(checkOut)
                    .status(BookingStatus.CONFIRMED)
                    .build();

            bookingRepository.save(booking);

            return new BookingResponse(
                    true,
                    bookingCode,
                    request.customerName(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    request.checkInTime(),
                    request.checkOutTime(),
                    room.getPricePerNight(),
                    "Đặt phòng thành công!"
            );

        } catch (Exception e) {
            log.error("Lỗi khi xử lý đặt phòng trong Tool: {}", e.getMessage(), e);
            return new BookingResponse(false, null, request.customerName(), request.roomNumber(), null,
                    request.checkInTime(), request.checkOutTime(), 0.0,
                    "Lỗi định dạng hoặc xử lý hệ thống: " + e.getMessage() + ". Định dạng thời gian cần dùng là 'yyyy-MM-dd HH:mm'.");
        }
    }
}
