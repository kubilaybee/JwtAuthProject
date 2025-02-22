package com.auth.JWTAuth.config.security;

import com.auth.JWTAuth.constant.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  @Autowired private JwtFilter jwtFilter;
  @Autowired private AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeHttpRequests(
            req ->
                req.requestMatchers("/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/users/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PUT, "/users/change-role")
                    .hasAnyAuthority(AppConstants.UserRoles.SUPER.getValue())
                    .requestMatchers(HttpMethod.PUT, "/users")
                    .authenticated()
                    .requestMatchers("/test")
                    .authenticated()
                    .requestMatchers("/swagger-ui/**")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**")
                    .permitAll())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable);
    return httpSecurity.build();
  }
}
