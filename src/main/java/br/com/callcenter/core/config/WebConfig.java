package br.com.callcenter.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Origens permitidas via variável de ambiente CORS_ALLOWED_ORIGINS
     * (lista separada por vírgula). Default: origens de desenvolvimento local.
     */
    @Value("${app.cors.allowed-origins:http://localhost:3007,http://localhost:3000,http://localhost:5173}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .exposedHeaders("Content-Disposition")
                .maxAge(3600);
    }
}
