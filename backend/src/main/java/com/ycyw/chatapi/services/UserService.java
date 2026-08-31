package com.ycyw.chatapi.services;

import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.exceptions.ApiException;
import com.ycyw.chatapi.mappers.UserMapper;
import com.ycyw.chatapi.repositories.UserRepository;
import com.ycyw.chatapi.responses.UserResponse;

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
