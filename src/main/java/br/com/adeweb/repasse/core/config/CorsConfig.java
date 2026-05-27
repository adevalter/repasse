package br.com.adeweb.repasse.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();

            config.setAllowedOrigins(List.of(
                    "http://localhost:4200",
                    "http://127.0.0.1:4200",
                    "http://185.111.156.141:9595",
                    "http://185.111.156.141:4200",
                    "https://185.111.156.141:4200",
                    "https://awrepasse.adeapp.com"
            ));

            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS"));
            config.setExposedHeaders(List.of(  "Content-Disposition", // necessário para download de arquivos
                    "Origin", "Content-Type", "Accept", "Authorization",
                    "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }
}