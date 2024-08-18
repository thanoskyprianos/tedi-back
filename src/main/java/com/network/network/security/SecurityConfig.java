package com.network.network.security;

import com.network.network.security.jwt.JwtAuthFilter;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Resource
    JwtAuthFilter jwtAuthFilter;

    @Resource
    LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> {
            requests.requestMatchers("users/login").permitAll();
            requests.requestMatchers("users/register").permitAll();
            requests.requestMatchers("users/logout").permitAll();
            requests.requestMatchers("users/logout/success").permitAll();
            requests.anyRequest().authenticated();
        });

        http.csrf(AbstractHttpConfigurer::disable);

        http.httpBasic(Customizer.withDefaults());

        http.sessionManagement((session) ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling((exc) -> exc.authenticationEntryPoint(new AuthEntryPoint()));

        http.logout((logout) -> {
            logout.logoutUrl("/users/logout");
            logout.logoutSuccessUrl("/users/logout/success");
            logout.addLogoutHandler(logoutHandler);
        });

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
