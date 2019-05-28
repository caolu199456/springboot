package com.example.util.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

public class TokenUtils {

    /**
     * 秘钥
     */
    private static final byte[] DEFAULT_SECRET="3d990d2276917dfac04467df11fff26d".getBytes();


    /**
     * 生成永不过期token，该方法只在用户登录成功后调用 每次都能变化
     *
     * @param payload map集合，可以存储用户id，姓名 不能存放敏感信息
     * @param secret 加密的秘钥 如果为null采取默认秘钥
     * @return token字符串,若失败则返回null
     */
    public static String createToken(Map<String, String> payload,String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret == null ? DEFAULT_SECRET : secret.getBytes());
        JWTCreator.Builder builder = JWT.create();
        if (payload != null && payload.size() > 0) {
            for (Map.Entry<String, String> entry : payload.entrySet()) {
                builder = builder.withClaim(entry.getKey(), entry.getValue());
            }
        }
        builder = builder.withJWTId(UUID.randomUUID().toString());
        return builder.sign(algorithm);
    }


    /**
     * 生成可过期token，该方法只在用户登录成功后调用
     *
     * @param payload map集合，可以存储用户id，不能存放敏感信息
     * @param expiredAt 过期时间在什么时候过期
     * @return token字符串,若失败则返回null
     */
    public static String createToken(Map<String, String> payload, String secret, Date expiredAt) {
        Algorithm algorithm = Algorithm.HMAC256(secret == null ? DEFAULT_SECRET : secret.getBytes());
        JWTCreator.Builder builder = JWT.create();
        if (payload != null && payload.size() > 0) {
            for (Map.Entry<String, String> entry : payload.entrySet()) {
                builder = builder.withClaim(entry.getKey(), entry.getValue());
            }
        }
        builder = builder.withExpiresAt(expiredAt).withJWTId(UUID.randomUUID().toString());
        return builder.sign(algorithm);
    }
    /**
     * 该方法在过滤器中调用，每次请求API时都校验
     * @param token
     * @param secret
     * @param isCheckExpireTime 强制校验有效期没有有效期字段则会返回不合法
     * @return
     */
    public static CheckTokenResult checkToken(String token,String secret,boolean isCheckExpireTime) {
        CheckTokenResult checkTokenResult = new CheckTokenResult();
        checkTokenResult.setTokenState(TokenState.INVALID);
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret == null ? DEFAULT_SECRET : secret.getBytes());
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            Map<String, String> map = new HashMap();
            if (jwt.getClaims() != null && jwt.getClaims().size() > 0) {
                for (Map.Entry<String, Claim> entry : jwt.getClaims().entrySet()) {
                    map.put(entry.getKey(), entry.getValue().asString());
                }
            }

            if (!isCheckExpireTime) {
                checkTokenResult.setPayloadInfo(map);
                checkTokenResult.setTokenState(TokenState.VALID);
                return checkTokenResult;
            }
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt != null) {
                checkTokenResult.setPayloadInfo(map);
                checkTokenResult.setTokenState(TokenState.VALID);
                return checkTokenResult;
            }
            return checkTokenResult;
        } catch (Exception e) {
            return checkTokenResult;
        }


    }

    public static void main(String[] args) {
        Set<String> hashSet = new HashSet<>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String token = createToken(null, null);
            hashSet.add(token);
            System.out.println(token);
            System.out.println(checkToken(token, null, false));
        }
        System.out.println(System.currentTimeMillis()-l);
        System.out.println(hashSet.size());


    }
    @AllArgsConstructor
    public enum TokenState implements Serializable {
        /**
         * 合法
         */
        VALID(1),
        /**
         * 不合法
         */
        INVALID(2);
        @Getter@Setter
        private int value;
    }
    @Data
    public static class CheckTokenResult implements Serializable {
        private Map<String,String> payloadInfo;
        private TokenState tokenState;

    }


}
