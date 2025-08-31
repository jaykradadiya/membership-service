package com.membership.program.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    public final static long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSignInKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaimFromToken(String token, Function<Claims,T> claimResovler){
        final Claims claims = getAllClaimsFromToken(token);
        return  claimResovler.apply(claims);
    }

    public String getUsernameFromToken(String token){
        return  getClaimFromToken(token,Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }

    private Boolean isTokenExpire(String token){
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }


    private String doGenerateToken(Map<String,Object> claims, String subject){
        return Jwts
                .builder()
                .claims().add(claims)
                .and()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails){
        Map<String,Object> Claims = new HashMap<>();
        return doGenerateToken(Claims,userDetails.getUsername());
    }

    public String generateToken(UserDetails userDetails, Long userId){
        Map<String,Object> Claims = new HashMap<>();
        Claims.put("userId", userId);
        return doGenerateToken(Claims,userDetails.getUsername());
    }

    public Long getUserIdFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            return getUserIdFromToken(token);
        }
        return null;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token,UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return userDetails.getUsername().equals(username) && !isTokenExpire(token);
    }
}
