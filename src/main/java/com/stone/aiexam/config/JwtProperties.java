package com.stone.aiexam.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * JWT 配置类 — 将 application.yaml 中 jwt.* 配置一次性绑定
 *
 * <p>使用 {@code @Validated} 启动时校验，配置不合法直接拒绝启动。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64 编码的 HMAC 密钥，≥32 字节（256 bits）
     */
    private String secret;

    /**
     * Token 过期时间，单位毫秒
     */
    private Long expiration;
}