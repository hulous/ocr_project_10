package com.ycyw.chatapi.configurations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationProvider;

import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityConfigurationTest {

  @Test
  void writeUnauthorizedSets401Status() throws Exception {
    JwtAuthenticationFilter jwtAuthenticationFilter = Mockito.mock(JwtAuthenticationFilter.class);
    AuthenticationProvider authenticationProvider = Mockito.mock(AuthenticationProvider.class);
    SecurityConfiguration configuration = new SecurityConfiguration(jwtAuthenticationFilter, authenticationProvider);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    StringWriter output = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(output));

    Method method = SecurityConfiguration.class.getDeclaredMethod(
      "writeUnauthorized",
      HttpServletRequest.class,
      HttpServletResponse.class,
      org.springframework.security.core.AuthenticationException.class
    );
    method.setAccessible(true);
    method.invoke(configuration, request, response, null);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    assert output.toString().contains("Unauthorized request");
  }
}
