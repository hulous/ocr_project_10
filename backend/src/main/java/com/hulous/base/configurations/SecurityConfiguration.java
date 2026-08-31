package com.hulous.base.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Configuration
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;

  private static final String[] PUBLIC_URLS = {
    "/api/auth/login",
    "/api/auth/register",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/uploads/**"
  };

  public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.authenticationProvider = authenticationProvider;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    AuthenticationEntryPoint unauthorizedEntryPoint = this::writeUnauthorized;

    http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(PUBLIC_URLS)
        .permitAll()
        .anyRequest()
        .authenticated()
      )
      .exceptionHandling(exceptionHandling -> exceptionHandling
        .authenticationEntryPoint(unauthorizedEntryPoint)
        .accessDeniedHandler(
          (request, response, exception) -> writeUnauthorized(request, response, null)
        )
      )
      .sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(
        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
      )
      .formLogin(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable);

    return http.build();
  }

  private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write("{\"message\":\"Unauthorized request\"}");
  }
}
