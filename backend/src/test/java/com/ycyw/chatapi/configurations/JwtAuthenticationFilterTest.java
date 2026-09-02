package com.ycyw.chatapi.configurations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private UserDetailsService userDetailsService;

  @Mock private HandlerExceptionResolver handlerExceptionResolver;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldNotFilterReturnsTrueForPublicPaths() {
    TestableJwtAuthenticationFilter filter =
        new TestableJwtAuthenticationFilter(
            jwtService, userDetailsService, handlerExceptionResolver);
    when(request.getServletPath()).thenReturn("/api/auth/login");

    assertTrue(filter.callShouldNotFilter(request));
  }

  @Test
  void shouldNotFilterReturnsFalseForPrivatePath() {
    TestableJwtAuthenticationFilter filter =
        new TestableJwtAuthenticationFilter(
            jwtService, userDetailsService, handlerExceptionResolver);
    when(request.getServletPath()).thenReturn("/api/rentals");

    assertFalse(filter.callShouldNotFilter(request));
  }

  @Test
  void doFilterInternalDelegatesWhenHeaderMissing() throws Exception {
    TestableJwtAuthenticationFilter filter =
        new TestableJwtAuthenticationFilter(
            jwtService, userDetailsService, handlerExceptionResolver);
    when(request.getHeader("Authorization")).thenReturn(null);

    filter.callDoFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternalAuthenticatesWhenTokenIsValid() throws Exception {
    TestableJwtAuthenticationFilter filter =
        new TestableJwtAuthenticationFilter(
            jwtService, userDetailsService, handlerExceptionResolver);
    User user = new User().setEmail("john@example.com");

    when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");
    when(jwtService.extractUsername("jwt-token")).thenReturn("john@example.com");
    when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(user);
    when(jwtService.isTokenValid("jwt-token", user)).thenReturn(true);

    filter.callDoFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternalSkipsUserLoadWhenAlreadyAuthenticated() throws Exception {
    TestableJwtAuthenticationFilter filter =
        new TestableJwtAuthenticationFilter(
            jwtService, userDetailsService, handlerExceptionResolver);

    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("existing", null));

    when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");
    when(jwtService.extractUsername("jwt-token")).thenReturn("john@example.com");

    filter.callDoFilterInternal(request, response, filterChain);

    verify(userDetailsService, never()).loadUserByUsername(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternalUsesExceptionResolverOnFailure() throws Exception {
    TestableJwtAuthenticationFilter filter =
        new TestableJwtAuthenticationFilter(
            jwtService, userDetailsService, handlerExceptionResolver);

    when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");
    when(jwtService.extractUsername("jwt-token")).thenThrow(new RuntimeException("boom"));

    filter.callDoFilterInternal(request, response, filterChain);

    verify(handlerExceptionResolver).resolveException(any(), any(), any(), any());
  }

  private static class TestableJwtAuthenticationFilter extends JwtAuthenticationFilter {
    TestableJwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService,
        HandlerExceptionResolver handlerExceptionResolver) {
      super(jwtService, userDetailsService, handlerExceptionResolver);
    }

    boolean callShouldNotFilter(HttpServletRequest request) {
      return super.shouldNotFilter(request);
    }

    void callDoFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
      super.doFilterInternal(request, response, chain);
    }
  }
}
