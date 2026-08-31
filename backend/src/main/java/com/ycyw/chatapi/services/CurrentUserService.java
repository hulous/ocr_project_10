package com.ycyw.chatapi.services;

import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.exceptions.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Provides access to the currently authenticated user for services that need it.
 * This centralizes the retrieval of the current user from the security context,
 * making it easier to test and maintain services that need user information.
 */
@Component
public class CurrentUserService {

  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @return the currently authenticated User
   * @throws ApiException if no user is authenticated or the principal is not a User instance
   */
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication.getPrincipal() == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "No authenticated user found");
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof User user)) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized request");
    }

    return user;
  }

  /**
   * Safely retrieves the currently authenticated user, returning null if no user is authenticated.
   *
   * @return the currently authenticated User, or null if not authenticated
   */
  public User getCurrentUserOrNull() {
    try {
      return getCurrentUser();
    } catch (ApiException e) {
      return null;
    }
  }
}
