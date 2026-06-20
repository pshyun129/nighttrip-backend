package com.ssafy.nighttrip.user.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ssafy.nighttrip.global.config.CloudinaryProperties;
import com.ssafy.nighttrip.user.dto.ProfileImageSignatureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudinarySignatureService {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    public ProfileImageSignatureResponse generateProfileImageSignature(Long userId) {
        long timestamp = System.currentTimeMillis() / 1000;

        String folder = cloudinaryProperties.getFolder() + "/user_" + userId;
        String publicId = "profile_" + UUID.randomUUID();

        Map<String, Object> paramsToSign = new TreeMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", folder);
        paramsToSign.put("public_id", publicId);
        paramsToSign.put("overwrite", true);

        String signature = sign(paramsToSign);

        String uploadUrl = "https://api.cloudinary.com/v1_1/"
                + cloudinaryProperties.getCloudName()
                + "/image/upload";

        return new ProfileImageSignatureResponse(
                cloudinaryProperties.getCloudName(),
                cloudinaryProperties.getApiKey(),
                timestamp,
                signature,
                folder,
                publicId,
                uploadUrl
        );
    }

    public boolean isOwnedProfileImage(Long userId, String publicId) {
        String expectedPrefix = cloudinaryProperties.getFolder() + "/user_" + userId + "/";
        return publicId != null && publicId.startsWith(expectedPrefix);
    }

    public boolean isValidUploadResponseSignature(String publicId, Long version, String signature) {
        String stringToSign = "public_id=" + publicId
                + "&version=" + version
                + cloudinaryProperties.getApiSecret();

        String expectedSignature = sha1Hex(stringToSign);

        return expectedSignature.equals(signature);
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
        } catch (Exception e) {
            // 실제 프로젝트에서는 log.warn 정도 남기면 됨
            // 기존 이미지 삭제 실패 때문에 프로필 수정 전체를 실패시키지는 않음
        }
    }

    private String sign(Map<String, Object> paramsToSign) {
        String stringToSign = paramsToSign.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> !String.valueOf(entry.getValue()).isBlank())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return sha1Hex(stringToSign + cloudinaryProperties.getApiSecret());
    }

    private String sha1Hex(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] digest = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Cloudinary signature 생성에 실패했습니다.", e);
        }
    }
}