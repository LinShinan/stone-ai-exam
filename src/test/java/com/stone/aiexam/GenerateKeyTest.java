package com.stone.aiexam;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;

/**
 * 临时工具：生成 HS256 密钥的 Base64 字符串，跑完复制到 yaml 后可以删掉
 */
public class GenerateKeyTest {

    @Test
    void generateSecretKey() {
        String base64Key = Encoders.BASE64.encode(
                Jwts.SIG.HS256.key().build().getEncoded()
        );
        System.out.println("jwt.secret: " + base64Key);
    }
}