package ai_intergration.ptit_cntt1_it213_session13.service;

import ai_intergration.ptit_cntt1_it213_session13.dto.ChatRequest;
import ai_intergration.ptit_cntt1_it213_session13.dto.ChatResponseDto;
import ai_intergration.ptit_cntt1_it213_session13.tool.HotelBookingTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final HotelBookingTool hotelBookingTool;


    public ChatResponseDto handleChat(ChatRequest chatRequest) {
        try {
            String conversationId = (chatRequest.getConversationId() != null && !chatRequest.getConversationId().isBlank())
                    ? chatRequest.getConversationId()
                    : "default-session";

            ChatResponse response = chatClient.prompt()
                    .user(chatRequest.getMessage())
                    .tools(hotelBookingTool)
                    .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                    .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                    .call()
                    .chatResponse();

            List<String> contentLines = extractContentLines(response);
            List<String> sourceDocs = extractSourceDocuments(response);

            return ChatResponseDto.builder()
                    .content(contentLines)
                    .sourceDocuments(sourceDocs)
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi xử lý hội thoại: {}", e.getMessage(), e);
            return ChatResponseDto.builder()
                    .content(List.of("Đã xảy ra lỗi khi kết nối với trợ lý AI: " + e.getMessage()))
                    .sourceDocuments(List.of())
                    .build();
        }
    }

    private List<String> extractContentLines(ChatResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            return List.of("Không có nội dung phản hồi.");
        }

        String rawText = response.getResult().getOutput().getText();
        if (rawText == null || rawText.isBlank()) {
            return List.of("Không có nội dung phản hồi.");
        }

        return Arrays.stream(rawText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    private List<String> extractSourceDocuments(ChatResponse response) {
        if (response == null || response.getMetadata() == null) {
            return List.of();
        }

        List<Document> docs = response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        if (docs == null || docs.isEmpty()) {
            return List.of();
        }

        return docs.stream()
                .map(this::formatDocumentSource)
                .toList();
    }

    private String formatDocumentSource(Document doc) {
        String fileName = (String) doc.getMetadata().getOrDefault("file_name", "Tài liệu QuickStay Hotel");
        String text = doc.getText() != null ? doc.getText().trim().replaceAll("\\s+", " ") : "";
        if (text.length() > 180) {
            text = text.substring(0, 180) + "...";
        }
        return fileName + ": " + text;
    }
}
