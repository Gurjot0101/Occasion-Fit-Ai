package com.occasionfit.backend.agent.tools.impl;

import com.occasionfit.backend.agent.AgentContext;
import com.occasionfit.backend.agent.tools.AgentTool;
import com.occasionfit.backend.agent.tools.ToolExecutionResult;
import com.occasionfit.backend.agent.tools.ToolExecutor;
import com.occasionfit.backend.ai.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GenerateTextResponseTool implements ToolExecutor {

    private final GeminiClient geminiChatService;

    @Override
    public AgentTool getToolType() {
        return AgentTool.GENERATE_TEXT_RESPONSE;
    }

    @Override
    public ToolExecutionResult execute(Map<String, Object> inputs, AgentContext ctx) {
        String result = ctx.hasExecutedActions()
                ? geminiChatService.synthesizeFinalResponse(ctx)
                : geminiChatService.generateResponse(ctx.getThreadContext(), ctx.getUserMessage(), ctx.getRecentMessages());
        return ToolExecutionResult.success(result);
    }
}