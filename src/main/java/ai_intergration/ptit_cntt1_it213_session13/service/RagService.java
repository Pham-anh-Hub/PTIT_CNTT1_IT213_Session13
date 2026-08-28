package ai_intergration.ptit_cntt1_it213_session13.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final VectorStore vectorStore;


    public String loadAndSaveDocument(MultipartFile file) {
        try {
            Resource resource = file.getResource();
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
            List<Document> rawDocument = tikaDocumentReader.get();

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder().build();
            List<Document> documents = tokenTextSplitter.split(rawDocument);

            vectorStore.add(documents);
            log.info("Đã lưu {} đoạn thông tin từ tài liệu {} vào VectorStore thành công.", documents.size(), resource.getFilename());
            return "Lưu dữ liệu thông tin tài liệu thành công";
        } catch (Exception e) {
            log.error("Lỗi khi xử lý lưu tài liệu: {}", e.getMessage(), e);
            return e.getMessage();
        }
    }


    public String searchDocument(String keyword) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(keyword)
                        .topK(3)
                        .build()
        );
        return documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
    }
}
