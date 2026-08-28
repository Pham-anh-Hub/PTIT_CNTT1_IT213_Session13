package ai_intergration.ptit_cntt1_it213_session13.tool;

import ai_intergration.ptit_cntt1_it213_session13.model.Room;
import ai_intergration.ptit_cntt1_it213_session13.model.constant.RoomStatus;
import ai_intergration.ptit_cntt1_it213_session13.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomMcpTool {

    private final RoomRepository roomRepository;

    public record CreateRoomRequest(
            String roomNumber,
            String roomType,
            Double pricePerNight,
            Integer capacity,
            String description
    ) {}

    @Tool(name = "getAvailableRooms", description = "Truy vấn danh sách các phòng khách sạn đang ở trạng thái còn trống (AVAILABLE).")
    public List<Room> getAvailableRooms() {
        log.info("MCP Tool getAvailableRooms được gọi.");
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    @Tool(name = "createRoom", description = "Tạo mới một phòng trong hệ thống QuickStay Hotel với đầy đủ thông tin.")
    public Room createRoom(CreateRoomRequest request) {
        log.info("MCP Tool createRoom được gọi với request: {}", request);
        Room room = Room.builder()
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .pricePerNight(request.pricePerNight())
                .capacity(request.capacity())
                .description(request.description())
                .status(RoomStatus.AVAILABLE)
                .build();
        return roomRepository.save(room);
    }
}
