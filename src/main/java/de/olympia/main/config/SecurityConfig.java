package de.olympia.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                /*
                * replace the above line with the following if you want to restrict registration to admins only
                * ---------------------------------------------------------------------------------------------
                * .requestMatchers("/api/auth/login").permitAll()
                * .requestMatchers("/api/auth/register").hasRole("ADMIN")
                * */
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});

        return http.build();
    }
}

