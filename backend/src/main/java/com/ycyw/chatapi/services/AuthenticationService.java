package com.ycyw.chatapi.services;

import com.ycyw.chatapi.dtos.LoginUserDto;
import com.ycyw.chatapi.dtos.RegisterUserDto;
import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.mappers.UserMapper;
import com.ycyw.chatapi.repositories.UserRepository;
import com.ycyw.chatapi.responses.LoginResponse;
import com.ycyw.chatapi.responses.UserResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserMapper userMapper;
  private final CurrentUserService currentUserService;

  public AuthenticationService(
      UserRepository userRepository,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      UserMapper userMapper,
      CurrentUserService currentUserService
  ) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.userMapper = userMapper;
    this.currentUserService = currentUserService;
  }

  public User registrate(RegisterUserDto input) {
    if (userRepository.existsByEmail(input.getEmail())) {
      throw new IllegalArgumentException("A user with this email already exists");
    }

    User user = new User()
      .setName(input.getName())
      .setEmail(input.getEmail())
      .setPassword(passwordEncoder.encode(input.getPassword()));

    return userRepository.save(user);
  }

  public User authenticate(LoginUserDto input) {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword());
    authenticationManager.authenticate(authToken);

    return userRepository
      .findByEmail(input.getEmail())
      .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
  }

  public UserResponse registrateResponse(RegisterUserDto registerUserDto) {
    User registeredUser = registrate(registerUserDto);

    return userMapper.toResponse(registeredUser);
  }

  public LoginResponse authenticateResponse(LoginUserDto loginUserDto) {
    User authenticatedUser = authenticate(loginUserDto);
    String jwtToken = jwtService.generateToken(authenticatedUser);

    return new LoginResponse()
      .setToken(jwtToken)
      .setExpiresIn(jwtService.getExpirationTime());
  }

  public UserResponse authenticatedUser() {
    User currentUser = currentUserService.getCurrentUser();

    return userMapper.toResponse(currentUser);
  }
}
