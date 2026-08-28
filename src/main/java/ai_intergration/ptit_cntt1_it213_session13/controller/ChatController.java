package ai_intergration.ptit_cntt1_it213_session13.controller;

import ai_intergration.ptit_cntt1_it213_session13.dto.ChatRequest;
import ai_intergration.ptit_cntt1_it213_session13.dto.ChatResponseDto;
import ai_intergration.ptit_cntt1_it213_session13.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequest request) {
        ChatResponseDto response = chatService.handleChat(request);
        return ResponseEntity.ok(response);
    }
}
