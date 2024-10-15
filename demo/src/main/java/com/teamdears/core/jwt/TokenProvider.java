package com.teamdears.core.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider implements InitializingBean {
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS_TEST = 60; // 테스트용 AT는 1분
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS_TEST = 10; // 테스트용 RT는 1분
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60; // access token은 24시간
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60 * 14; // refresh token은 2주일

    private Key key;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsername(String token) {
        try {
            key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("claims.getSubject() = " + claims.getSubject());
            return claims.getSubject();
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return null;
    }

    public String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && StringUtils.startsWithIgnoreCase(bearerToken, "Bearer ")) {
            return bearerToken.substring(7);
        }
        // 쿠키에서 토큰 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    System.out.println("cookie.getName() = " + cookie.getName());
                    System.out.println("cookie.getValue() = " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        throw new RuntimeException("No AccessToken Found");
    }

    public String getUniqueId(String accessToken) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
        return claims.get("uniqueId", String.class);
    }

    public String createRefreshToken(String username, String uniqueId) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + REFRESH_TOKEN_VALIDITY_SECONDS * 1000);

        return Jwts.builder()
                .setSubject(username)
                .claim("uniqueId", uniqueId) // 커스텀 클레임으로 uniqueId 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public String createAccessToken(String username, String uniqueId) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + ACCESS_TOKEN_VALIDITY_SECONDS * 1000);

        return Jwts.builder()
                .setSubject(username)
                .claim("uniqueId", uniqueId) // 커스텀 클레임으로 uniqueId 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public String getTokenUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public long getRefreshTokenExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public long getRefreshTokenExpirationByManual(String token) throws JsonProcessingException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }
        String payload = new String(Base64.getDecoder().decode(parts[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode claimsNode = objectMapper.readTree(payload);
        long expirationTime = claimsNode.get("exp").asLong() * 1000;
        return expirationTime - System.currentTimeMillis();
    }

    public String createdAdminJwtToken(String username, String uniqueId) {
        // Setting expiration to a future date (e.g., 10 years)
        long expirationTime = 1000L * 60 * 60 * 24 * 365 * 10;  // 10 years in milliseconds

        long now = (new Date()).getTime();
        Date validity = new Date(now + expirationTime);

        // Creating JWT token
        return Jwts.builder()
                .setSubject(username)
                .claim("uniqueId", uniqueId) // 커스텀 클레임으로 uniqueId 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }
}
