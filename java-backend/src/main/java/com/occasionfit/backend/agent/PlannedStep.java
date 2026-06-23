package com.occasionfit.backend.agent;

import com.occasionfit.backend.agent.tools.AgentTool;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class PlannedStep {
    private AgentTool tool;

    @Builder.Default
    private Map<String, Object> inputs = new HashMap<>();
}