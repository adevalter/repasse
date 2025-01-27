package br.com.adeweb.repasse.core.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

public class CustomCorsConfiguration implements CorsConfigurationSource {
    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200","http://185.111.156.141:9595","http://185.111.156.141:4200","https://185.111.156.141:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS"));
        config.setExposedHeaders(List.of("ORIGIN", "CONTENT-TYPE", "ACCEPT", "AUTHORIZATION",
                "ACCESS-CONTROL-ALLOW-ORIGIN", "ACCESS-CONTROL-ALLOW-ORIGIN", "ACCESS-CONTROL-ALLOW-CREDENTIALS"));
        config.setAllowedHeaders(List.of("*"));
        return config;
    }
}
