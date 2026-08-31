package com.hulous.base.services;

import com.hulous.base.entities.User;
import io.jsonwebtoken.ExpiredJwtException;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

  private static final String TEST_SECRET = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";

  @Test
  void generateAndValidateTokenWithDefaultClaims() {
    JwtService service = new JwtService();
    ReflectionTestUtils.setField(service, "secretKey", TEST_SECRET);
    ReflectionTestUtils.setField(service, "jwtExpiration", 3600000L);

    User user = new User().setEmail("alice@example.com");

    String token = service.generateToken(user);

    assertNotNull(token);
    assertEquals("alice@example.com", service.extractUsername(token));
    assertTrue(service.isTokenValid(token, user));
    assertEquals(3600000L, service.getExpirationTime());
  }

  @Test
  void generateTokenWithExtraClaimsAndMismatchedUserReturnsFalse() {
    JwtService service = new JwtService();
    ReflectionTestUtils.setField(service, "secretKey", TEST_SECRET);
    ReflectionTestUtils.setField(service, "jwtExpiration", 3600000L);

    User user = new User().setEmail("alice@example.com");
    User other = new User().setEmail("bob@example.com");

    String token = service.generateToken(Map.of("role", "USER"), user);

    assertFalse(service.isTokenValid(token, other));
  }

  @Test
  void expiredTokenThrowsExpiredJwtException() {
    JwtService service = new JwtService();
    ReflectionTestUtils.setField(service, "secretKey", TEST_SECRET);
    ReflectionTestUtils.setField(service, "jwtExpiration", -1000L);

    User user = new User().setEmail("alice@example.com");

    String token = service.generateToken(user);

    assertThrows(ExpiredJwtException.class, () -> service.isTokenValid(token, user));
  }
}
