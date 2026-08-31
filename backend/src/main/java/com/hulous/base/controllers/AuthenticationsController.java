package com.hulous.base.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulous.base.dtos.LoginUserDto;
import com.hulous.base.dtos.RegisterUserDto;
import com.hulous.base.responses.ApiMessageResponse;
import com.hulous.base.responses.LoginResponse;
import com.hulous.base.responses.UserResponse;
import com.hulous.base.services.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RequestMapping("/api/auth")
@RestController
@Tag(name = "Authentication", description = "Authentication and account endpoints")
public class AuthenticationsController {
  private final AuthenticationService authenticationService;

  public AuthenticationsController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/register")
  @Operation(summary = "Register a new user")
  @SecurityRequirements
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User created", content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid registration payload (validation error)", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class)))
  })
  public ResponseEntity<UserResponse> registrate(@Valid @RequestBody RegisterUserDto registerUserDto) {
    return ResponseEntity.ok(authenticationService.registrateResponse(registerUserDto));
  }

  @PostMapping("/login")
  @Operation(summary = "Authenticate user and generate JWT")
  @SecurityRequirements
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Authenticated", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class)))
  })
  public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
    return ResponseEntity.ok(authenticationService.authenticateResponse(loginUserDto));
  }

  @GetMapping("/me")
  @Operation(
    summary = "Display current user information",
    security = { @SecurityRequirement(name = "bearerAuth") }
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Current authenticated user", content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid token or unauthorized request", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
    @ApiResponse(responseCode = "403", description = "Authentication required", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class)))
  })
  public ResponseEntity<UserResponse> authenticatedUser() {
    return ResponseEntity.ok(authenticationService.authenticatedUser());
  }
}
