package com.dot.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final OpenAiService openAiService;

    @Value("${openai.model}")
    private String model;

    /**
     * K, P를 바탕으로 T 자동 제안
     */
    public String suggestTry(String keep, String problem) {
        String prompt = """
                당신은 개발자의 회고(KPT)를 돕는 코치입니다.

                아래는 오늘의 Keep(잘한 점)과 Problem(개선할 점)입니다.

                [Keep]
                %s

                [Problem]
                %s

                위 내용을 바탕으로 Try(다음에 시도할 것) 3가지를 구체적이고 실행 가능하게 제안해주세요.
                각 항목은 번호를 붙여 간결하게 작성하고, 실제 행동으로 옮길 수 있는 내용이어야 합니다.
                """.formatted(
                keep != null ? keep : "(없음)",
                problem != null ? problem : "(없음)"
        );

        return callGPT(prompt);
    }

    /**
     * K/P/T 작성 가이드 제공
     */
    public String getWritingGuide(String field) {
        String prompt = switch (field.toLowerCase()) {
            case "keep" -> """
                    KPT 회고의 Keep(잘한 점)을 효과적으로 작성하는 방법을 안내해주세요.
                    좋은 예시와 나쁜 예시를 포함해서, 개발자가 실제로 활용할 수 있도록 간결하게 설명해주세요.
                    """;
            case "problem" -> """
                    KPT 회고의 Problem(개선할 점)을 효과적으로 작성하는 방법을 안내해주세요.
                    문제를 구체적으로 파악하고 원인까지 기술하는 좋은 예시와 나쁜 예시를 포함해주세요.
                    """;
            case "try" -> """
                    KPT 회고의 Try(다음 시도)를 효과적으로 작성하는 방법을 안내해주세요.
                    실행 가능하고 측정 가능한 Try를 작성하는 방법을 예시와 함께 설명해주세요.
                    """;
            default -> "KPT 회고 전체 작성 방법을 간결하게 안내해주세요.";
        };

        return callGPT(prompt);
    }

    private String callGPT(String prompt) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(new ChatMessage("user", prompt)))
                    .maxTokens(1000)
                    .temperature(0.7)
                    .build();

            return openAiService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("AI 서비스 오류가 발생했습니다.");
        }
    }
}
