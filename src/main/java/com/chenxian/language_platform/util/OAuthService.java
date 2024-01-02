package com.chenxian.language_platform.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
public class OAuthService {

    @Value("${google.oauth2.client-id}")
    private String clientId;

    @Value("${google.oauth2.client-secret}")
    private String clientSecret;

    @Value("${google.oauth2.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public OAuthService() {
        this.restTemplate = new RestTemplate();
    }

    // 获取授权 URL
    public String getAuthorizationUrl(String responseType, String scope) {
        String additionalScopes = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
        return String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s %s",
                clientId, redirectUri, responseType, scope, additionalScopes
        );
    }

    // 通过授权码获取 Token 信息（包括 ID Token 和 Access Token）
    public JSONObject getTokenInfo(String authorizationCode) throws Exception {
        String response = Request.Post("https://oauth2.googleapis.com/token")
                .bodyForm(Form.form()
                        .add("code", authorizationCode)
                        .add("client_id", clientId)
                        .add("client_secret", clientSecret)
                        .add("redirect_uri", redirectUri)
                        .add("grant_type", "authorization_code")
                        .build())
                .execute().returnContent().asString();

        return new JSONObject(response);
    }

    // 解析 ID Token 并获取其 JWT 载荷
    public JWTClaimsSet extractJWTPayload(String idToken) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        return signedJWT.getJWTClaimsSet();
    }

    // 验证 ID Token 并获取声明集
    public JWTClaimsSet getClaimsSetAndVerifyIdToken(String idToken) throws Exception {
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWKSource keySource = new RemoteJWKSet(new URL("https://www.googleapis.com/oauth2/v3/certs"));
        JWSKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        String expectedAudience = clientId;
        JWTClaimsSet claimsSet = jwtProcessor.process(idToken, null);

        String issuer = claimsSet.getIssuer();
        List<String> audience = claimsSet.getAudience();
        Date expirationTime = claimsSet.getExpirationTime();
        if (!"https://accounts.google.com".equals(issuer) || !audience.contains(expectedAudience)) {
            throw new BadJWTException("Issuer or audience is invalid");
        }
        if (new Date().after(expirationTime)) {
            throw new BadJWTException("Token has expired");
        }
        return claimsSet;
    }

    // 其他方法...
}

