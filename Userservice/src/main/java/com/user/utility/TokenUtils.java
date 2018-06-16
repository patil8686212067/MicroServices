package com.user.utility;

import java.util.Date;


import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {
	private  final String key = "QwErTyUiOp";

	public  String generateToken(int id) {
		long currentTime = System.currentTimeMillis();
		Date currentDate = new Date(currentTime);
		Date expireDate = new Date(currentTime + 24 * 60 * 60 * 1000);

		JwtBuilder builder = Jwts.builder().setId(Integer.toString(id)).setIssuedAt(currentDate)
				.signWith(SignatureAlgorithm.HS256, key).setExpiration(expireDate);
		String generatedToken = builder.compact();
		return generatedToken;
	}

	public  int verifyToken(String token) {
		int id = 0;
		Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
		System.out.println("ID: " + claims.getId());
		id = Integer.parseInt(claims.getId());
		return id;
	}
}