package com.ssafy.nighttrip.user.dto;

// 서명 발급 응답
public record ProfileImageSignatureResponse(
        String cloudName,
        String apiKey,
        Long timestamp,
        String signature,
        String folder,
        String publicId,
        String uploadUrl
) {
}