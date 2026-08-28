package ai_intergration.ptit_cntt1_it213_session13.config;

import ai_intergration.ptit_cntt1_it213_session13.tool.RoomMcpTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider roomMcpToolCallbackProvider(RoomMcpTool roomMcpTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(roomMcpTool)
                .build();
    }
}
