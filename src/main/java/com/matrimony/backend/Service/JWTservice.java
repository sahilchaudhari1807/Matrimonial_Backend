package com.matrimony.backend.Service;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;



@Service
public class JWTservice {

	private static final String secret_key="mysuperzSecretsuperkeymysupersecretsuperkey123";
	
	public String generateToken(String username) {

	    Map<String,Object> claims=new HashMap<>();

	    return Jwts.builder()
	             .setClaims(claims)
	             .setSubject(username)
	             .setIssuedAt(new Date())
	             .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
	             .signWith(getSignKey(),SignatureAlgorithm.HS256)
	             .compact();
	}
		private SecretKey getSignKey() {
			byte [] bytes=secret_key.getBytes();
			return Keys.hmacShaKeyFor(bytes);
		}
		
		
		
		
		// from this code is to validate jwt
		// Extract username from JWT
		public String extractUserName(String token) {
		    return extractAllClaims(token).getSubject();
		}

		// Validate JWT token
		public boolean validateToken(String token, UserDetails userDetail) {
		    String username = extractUserName(token);
		    return username.equals(userDetail.getUsername()) && !isTokenExpired(token);
		}

		/* ---------- helper methods ---------- */
		private Claims extractAllClaims(String token) {
			
			/*1. Parse the token
2. Verify the signature using secret key
3. Extract payload*/
		    return Jwts.parser()                 // ✅ correct for 0.13.0
		            .verifyWith(getSignKey())    // new method
		            .build()
		            .parseSignedClaims(token)
		            .getPayload();
		}

		// Check token expiration
		private boolean isTokenExpired(String token) {
		    return extractAllClaims(token)
		            .getExpiration()
		            .before(new Date());
		}

		
	}

