package com.occasionfit.backend.agent;

import com.occasionfit.backend.agent.tools.AgentTool;
import com.occasionfit.backend.agent.tools.ToolExecutionResult;
import com.occasionfit.backend.agent.tools.ToolExecutor;
import com.occasionfit.backend.ai.PlannerService;
import com.occasionfit.backend.ai.client.GeminiClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class AgentOrchestrator {

    private final PlannerService plannerService;
    private final GeminiClient geminiClient;
    private final Map<AgentTool, ToolExecutor> toolRegistry;

    @Autowired
    public AgentOrchestrator(PlannerService plannerService,
                             GeminiClient geminiClient,
                             List<ToolExecutor> tools) {
        this.plannerService = plannerService;
        this.geminiClient = geminiClient;
        this.toolRegistry = tools.stream()
                .collect(Collectors.toMap(ToolExecutor::getToolType, t -> t));
    }

    public AgentResult run(AgentContext ctx) {
        AgentPlan plan = plannerService.plan(ctx);
        log.info("Execution plan: {}", plan.getSteps().stream()
                .map(s -> s.getTool().name()).toList());

        String generatedImage = null;

        for (PlannedStep step : plan.getSteps()) {
            log.info("Executing tool: {} with inputs: {}", step.getTool(), step.getInputs());
            ToolExecutionResult result = executeTool(step, ctx);

            String contextResult;
            if (step.getTool() == AgentTool.GENERATE_OUTFIT_IMAGE && result.isSuccess()) {
                generatedImage = result.getOutput();
                contextResult = "Outfit image generated successfully.";
            } else {
                contextResult = result.isSuccess() ? result.getOutput() : result.getErrorMessage();
            }

            ctx.addAction(AgentAction.builder()
                    .tool(step.getTool())
                    .toolResult(contextResult)
                    .build());

            if (!result.isSuccess()) {
                log.warn("Tool {} failed — aborting plan", step.getTool());
                break;
            }
        }

        return buildResult(ctx, generatedImage);
    }

    private AgentResult buildResult(AgentContext ctx, String generatedImage) {
        AgentAction last = ctx.getExecutedActions().isEmpty()
                ? null
                : ctx.getExecutedActions().getLast();

        boolean lastProducedText = last != null && (
                last.getTool() == AgentTool.GENERATE_TEXT_RESPONSE ||
                        last.getTool() == AgentTool.DIRECT_REPLY
        );

        String text = lastProducedText
                ? last.getToolResult()
                : geminiClient.synthesizeFinalResponse(ctx);

        return new AgentResult(text, generatedImage);
    }

    private ToolExecutionResult executeTool(PlannedStep step, AgentContext ctx) {
        ToolExecutor executor = toolRegistry.get(step.getTool());
        if (executor == null) {
            log.error("No executor registered for tool: {}", step.getTool());
            return ToolExecutionResult.failure("Unsupported tool: " + step.getTool());
        }
        return executor.execute(step.getInputs(), ctx);
    }
}