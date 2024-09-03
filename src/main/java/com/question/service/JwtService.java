package com.question.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.core.codec.Decoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService 
{
	public static final String SECRET_KEY = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
	
	
	public String generateToken(String userName)
	{
		Map<String, Object> claims = new HashMap<String, Object>();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date((System.currentTimeMillis())))
				.setExpiration(new Date(System.currentTimeMillis() + 1000  *  60 * 30))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

	private Key getSignKey() 
	{
		byte[] sign = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(sign);
	}
}
