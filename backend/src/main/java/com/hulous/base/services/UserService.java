package com.hulous.base.services;

import com.hulous.base.entities.User;
import com.hulous.base.exceptions.ApiException;
import com.hulous.base.mappers.UserMapper;
import com.hulous.base.repositories.UserRepository;
import com.hulous.base.responses.UserResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  public UserResponse show(Integer id) {
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
      throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
    }

    return userMapper.toResponse(user);
  }
}
