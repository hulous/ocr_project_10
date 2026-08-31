package com.hulous.base.mappers;

import com.hulous.base.entities.User;
import com.hulous.base.responses.UserResponse;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    return new UserResponse()
      .setId(user.getId())
      .setName(user.getName())
      .setEmail(user.getEmail())
      .setCreatedAt(user.getCreatedAt())
      .setUpdatedAt(user.getUpdatedAt());
  }
}
