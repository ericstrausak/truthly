package ch.zhaw.truthly.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Bean
    @Profile("!test") // Nur in Nicht-Test-Umgebungen
    public SecurityFilterChain productionSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF für REST API deaktivieren
            .authorizeHttpRequests(authorize -> authorize
                // Statische Ressourcen (SPA)
                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/static/**", "/**/*.js", "/**/*.css", "/**/*.ico", "/**/*.png", "/**/*.svg").permitAll()
                
                // MongoDB Test Endpoint (für Überwachung)
                .requestMatchers(HttpMethod.GET, "/testmongodb").permitAll()
                
                // Öffentliche Lesezugriffe (für Frontend ohne Login)
                .requestMatchers(HttpMethod.GET, "/api/article/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/user/**").permitAll()
                
                // Artikel-Erstellung - nur für Autoren und Admins
                .requestMatchers(HttpMethod.POST, "/api/article").hasAnyRole("AUTHOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/article/**").hasAnyRole("AUTHOR", "ADMIN")
                
                // Benutzer-Management - nur Admins
                .requestMatchers(HttpMethod.POST, "/api/user").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/user/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasRole("ADMIN")
                
                // Fact-Checking - nur Fact-Checker und Admins
                .requestMatchers(HttpMethod.POST, "/api/factcheck").hasAnyRole("FACT_CHECKER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/factcheck/ai-verify/**").hasAnyRole("FACT_CHECKER", "ADMIN", "AUTHOR")
                .requestMatchers(HttpMethod.GET, "/api/factcheck/**").hasAnyRole("FACT_CHECKER", "ADMIN", "AUTHOR")
                
                // Service Endpoints - authentifizierte Benutzer
                .requestMatchers("/api/service/**").authenticated()
                
                // Alle anderen API-Endpunkte erfordern Authentifizierung
                .requestMatchers("/api/**").authenticated()
                
                // SPA Fallback - alles andere erlauben (für Client-side Routing)
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    @Profile("test") // Test-Konfiguration - alles erlauben
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://" + auth0Domain + "/");
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return converter;
    }

    @Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Scopes als Authorities hinzufügen
            Collection<String> scopes = jwt.getClaimAsStringList("scope");
            if (scopes != null) {
                scopes.forEach(scope -> authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope)));
            }
            
            // Rollen aus Custom Claims extrahieren
            // Auth0 speichert Custom Claims oft mit Namespace
            Collection<String> roles = jwt.getClaimAsStringList("https://truthly.app/roles");
            if (roles != null) {
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            }
            
            // Alternative: Single Role Claim
            String role = jwt.getClaimAsString("https://truthly.app/role");
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            
            // Fallback: Standard Rollen aus Auth0
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<String> stdRoles = (Collection<String>) rolesObj;
                stdRoles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
            }
            
            return authorities;
        };
    }
}
