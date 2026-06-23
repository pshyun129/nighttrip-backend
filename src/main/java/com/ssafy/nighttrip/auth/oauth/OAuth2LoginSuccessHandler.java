package com.ssafy.nighttrip.auth.oauth;

import com.ssafy.nighttrip.auth.service.GoogleOAuthService;
import com.ssafy.nighttrip.auth.service.SocialLoginCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final GoogleOAuthService googleOAuthService;
    private final SocialLoginCodeService socialLoginCodeService;

    @Value("${app.oauth2.frontend-callback-url}")
    private String frontendCallbackUrl;

    @Value("${app.oauth2.frontend-login-url}")
    private String frontendLoginUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        try {
            if (!(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
                redirectToLoginFailure(request, response);
                return;
            }

            String email = oidcUser.getClaimAsString("email");
            Boolean emailVerified = oidcUser.getClaim("email_verified");
            String name = oidcUser.getClaimAsString("name");

            Long userId = googleOAuthService.findOrCreateGoogleUser(
                    email,
                    emailVerified,
                    name
            );

            String loginCode = socialLoginCodeService.createCode(userId);

            invalidateSession(request);

            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendCallbackUrl)
                    .queryParam("code", loginCode)
                    .build()
                    .encode()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.warn("Google OAuth2 로그인 처리 실패", e);
            redirectToLoginFailure(request, response);
        }
    }

    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    private void redirectToLoginFailure(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendLoginUrl)
                .queryParam("error", "google-login-failed")
                .build()
                .encode()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}