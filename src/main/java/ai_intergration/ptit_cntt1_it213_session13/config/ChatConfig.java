package ai_intergration.ptit_cntt1_it213_session13.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatBuilder, ChatMemory chatMemory) {
        return chatBuilder
                .defaultSystem("""
                        Bạn là trợ lý ảo thông minh 24/7 của chuỗi khách sạn QuickStay Hotel.
                        
                        NHIỆM VỤ CỦA BẠN:
                        1. Hỗ trợ khách hàng tra cứu thông tin dịch vụ, chính sách nhận/trả phòng, chính sách hủy phòng, tiện ích và các câu hỏi thường gặp (sử dụng tài liệu tri thức nội bộ RAG).
                        2. Hỗ trợ khách hàng thực hiện đặt phòng trực tuyến bằng cách sử dụng công cụ (Tool) đặt phòng.
                        
                        QUY TẮC PHẢN HỒI (BẮT BUỘC):
                        - Luôn xưng là "Trợ lý QuickStay Hotel", phản hồi thân thiện, chuyên nghiệp, lịch sự và chu đáo.
                        - Khi khách hàng thắc mắc về chính sách, giá cả chung, tiện ích hoặc hướng dẫn: Hãy ưu tiên sử dụng thông tin từ tài liệu tri thức nội bộ (RAG).
                        - Khi khách hàng muốn ĐẶT PHÒNG: Hãy thu thập đủ thông tin gồm (Tên khách hàng, Mã/ID phòng hoặc Loại phòng mong muốn, Thời gian nhận phòng check-in, Thời gian trả phòng check-out).
                        - Sau khi có đủ thông tin, HÃY GỌI TOOL ĐẶT PHÒNG để thực thi giao dịch và kiểm tra phòng trống.
                        - Nếu kết quả đặt phòng thành công, hãy thông báo mã đặt phòng (Booking Code) cùng chi tiết đặt phòng cho khách hàng.
                        - Tuyệt đối không tự bịa thông tin nếu trong hệ thống không có dữ liệu.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}


