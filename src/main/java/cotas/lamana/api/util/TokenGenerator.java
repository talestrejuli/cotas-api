package cotas.lamana.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class TokenGenerator {
    public static String generateToken() {
        Algorithm algorithm = Algorithm.HMAC256("secreto");
        String token = JWT.create()
                .withIssuer("seuApp")
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000))  // 1 hora de expiração
                .sign(algorithm);
        return token;
    }

    public static void main(String[] args) {
        String token = generateToken();
        System.out.println("Token gerado: " + token);
    }
}