package xyz.pplax.mymail.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TokenUtils {

    private static final String SECRET_KEY = "liuanxu-hate-the-world";
    private static final long EXPIRATION_TIME = 36000000; // 10 hour

    /**
     * 生成token
     * @param username
     * @param password
     * @return
     */
    public static String generateToken(String username, String password) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("password", password);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 解析token并返回 Claims 对象
     * Claims claims = TokenUtils.parseToken(yourToken);
     * String username = claims.getSubject();
     * String password = (String) claims.get("password");
     * @param token
     * @return
     */
    public static Claims parseToken(String token) throws ExpiredJwtException {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

}
