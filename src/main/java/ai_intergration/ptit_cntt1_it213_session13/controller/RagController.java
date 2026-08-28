package ai_intergration.ptit_cntt1_it213_session13.controller;

import ai_intergration.ptit_cntt1_it213_session13.dto.DocumentRequest;
import ai_intergration.ptit_cntt1_it213_session13.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    /**
     * Endpoint tải lên tài liệu nội bộ vào PGVector Store qua multipart/form-data
     */
    @PostMapping( "/upload")
    public ResponseEntity<String> uploadDocument(@ModelAttribute DocumentRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            return ResponseEntity.badRequest().body("Tệp tài liệu không được để trống.");
        }
        String result = ragService.loadAndSaveDocument(request.getFile());
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint tìm kiếm/tra cứu nội dung tương đồng trong PGVector Store
     */
    @GetMapping("/search")
    public ResponseEntity<String> searchDocument(@RequestParam("query") String query) {
        String result = ragService.searchDocument(query);
        return ResponseEntity.ok(result);
    }
}
