package pl.electronicgradebook.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // Dodaj adresy URL dozwolonego źródła (np. adres Angulara)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Dodaj dozwolone metody HTTP
                .allowedHeaders("*") // Dodaj dozwolone nagłówki
                .allowCredentials(true);
    }
}