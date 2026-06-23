package com.occasionfit.backend.ai.prompt;

import com.occasionfit.backend.agent.AgentAction;
import com.occasionfit.backend.agent.AgentContext;

import java.util.stream.Collectors;

public class PromptBuilder {

    private static final int MAX_SYNTHESIS_RESULT_LENGTH = 500;

    public static String buildPlannerPrompt(AgentContext ctx) {
        return """
                You are a planner for a fashion assistant AI.
                Your job is to understand the user's intent from the conversation and decide which tools to run. Don't respond outside of available tools.
                
                Available tools:
                - DIRECT_REPLY           : greetings, small talk, off-topic. Always alone.
                - ANALYZE_OUTFIT_IMAGE   : analyze a single uploaded image
                - COMPARE_OUTFIT_IMAGES  : compare 2+ uploaded outfit images
                - GENERATE_OUTFIT_IMAGE  : generate a NEW outfit image. Only when user explicitly wants something new or different.
                - GENERATE_TEXT_RESPONSE : send a text response to the user
                
                Always end with GENERATE_TEXT_RESPONSE unless using DIRECT_REPLY.
                DIRECT_REPLY is always alone.
                
                For GENERATE_OUTFIT_IMAGE:
                - Only use when the user is requesting generation of something new, different, or modified.
                - Do NOT use when user is reacting, approving, thanking, or expressing satisfaction.
                - Always populate "prompt" in toolInputs with a vivid outfit description (max 300 chars) from the full conversation.
                
                Recent conversation:
                %s
                
                User message: %s
                Images uploaded: %d
                Thread context: %s
                
                Think step by step:
                1. What is the user's intent? (requesting something new / reacting / asking a question / greeting)
                2. Does this require generating a new image or just a text response?
                3. Which tools satisfy this intent?
                
                Respond ONLY in this exact JSON format, no markdown:
                {
                  "reasoning": "step by step reasoning here",
                  "steps": ["TOOL_1", "TOOL_2"],
                  "toolInputs": {
                    "GENERATE_OUTFIT_IMAGE": { "prompt": "..." }
                  }
                }
                """.formatted(
                formatRecentMessages(ctx),
                ctx.getUserMessage(),
                ctx.getBase64Images() != null ? ctx.getBase64Images().size() : 0,
                ctx.getThreadContext() != null ? ctx.getThreadContext() : "none"
        );
    }

    private static String formatRecentMessages(AgentContext ctx) {
        if (ctx.getRecentMessages() == null || ctx.getRecentMessages().isEmpty()) return "none";
        return ctx.getRecentMessages().stream()
                .map(m -> m.getSender() + ": " + m.getText())
                .collect(Collectors.joining("\n"));
    }

    public static String buildSynthesisPrompt(AgentContext ctx) {
        StringBuilder prompt = new StringBuilder();

        prompt.append(PromptTemplate.system());

        if (ctx.getThreadContext() != null && !ctx.getThreadContext().isEmpty())
            prompt.append("\nContext: ").append(ctx.getThreadContext());

        prompt.append("\n\nUser: ").append(ctx.getUserMessage());

        if (!ctx.getExecutedActions().isEmpty()) {
            prompt.append("\n\nInformation gathered:");
            for (AgentAction action : ctx.getExecutedActions()) {
                prompt.append("\n- ").append(truncate(action.getToolResult()));
            }
        }

        prompt.append("\n\nBased on the above, respond to the user:");
        return prompt.toString();
    }

    private static String truncate(String text) {
        if (text == null) return null;
        return text.length() > MAX_SYNTHESIS_RESULT_LENGTH
                ? text.substring(0, MAX_SYNTHESIS_RESULT_LENGTH) + "..."
                : text;
    }
}