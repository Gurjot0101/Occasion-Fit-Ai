package com.occasionfit.backend.agent.tools.impl;

import com.occasionfit.backend.agent.AgentContext;
import com.occasionfit.backend.agent.tools.AgentTool;
import com.occasionfit.backend.agent.tools.ToolExecutionResult;
import com.occasionfit.backend.agent.tools.ToolExecutor;
import com.occasionfit.backend.ai.client.PollinationsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class GenerateOutfitImageTool implements ToolExecutor {

    private final PollinationsClient pollinationsClient;

    @Override
    public AgentTool getToolType() {
        return AgentTool.GENERATE_OUTFIT_IMAGE;
    }

    @Override
    public ToolExecutionResult execute(Map<String, Object> inputs, AgentContext ctx) {
        try {
            String prompt = (String) inputs.get("prompt");
            log.info("Generating outfit image with prompt: {}", prompt);
            String imageUrl = pollinationsClient.generateOutfitImage(prompt);
            return ToolExecutionResult.success(imageUrl);
        } catch (Exception e) {
            log.warn("Image generation failed: {}", e.getMessage());
            return ToolExecutionResult.failure("Image generation is currently unavailable, please try again.");
        }
    }
}