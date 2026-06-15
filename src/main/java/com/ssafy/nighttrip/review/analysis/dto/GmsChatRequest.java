package com.ssafy.nighttrip.review.analysis.dto;

import java.util.List;

public record GmsChatRequest(
        String model,
        List<Message> messages
) {

    public record Message(
            String role,
            String content
    ) {
    }
}