package com.occasionfit.backend.agent.tools;

import com.occasionfit.backend.agent.AgentContext;

import java.util.Map;

public interface ToolExecutor {
    AgentTool getToolType();

    ToolExecutionResult execute(Map<String, Object> inputs, AgentContext ctx);
}