package br.com.adeweb.repasse.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfiguration {

    @Bean
    public WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200","http://185.111.156.141:9595","http://185.111.156.141:4200","https://185.111.156.141:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, HttpHeaders.ORIGIN,
                                HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION)
                        .allowCredentials(true);
            }
        };
    }
}
//"GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT","AUTHORIZATION"