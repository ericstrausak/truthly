package ch.zhaw.truthly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // CSRF deaktivieren für Testing
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()   // ALLES ERLAUBEN - keine Authentifizierung nötig
            );
            // OAuth2 Resource Server komplett entfernt für Testing

        return http.build();
    }
}