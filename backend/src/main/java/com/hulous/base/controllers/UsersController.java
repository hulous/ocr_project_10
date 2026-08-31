package com.hulous.base.controllers;

import com.hulous.base.responses.ApiMessageResponse;
import com.hulous.base.responses.UserResponse;
import com.hulous.base.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/user")
@RestController
@Tag(name = "Users", description = "User resource endpoints")
public class UsersController {
  private final UserService userService;

  public UsersController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  @Operation(
    summary = "Show one user by id",
    security = { @SecurityRequirement(name = "bearerAuth") }
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
    @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class)))
  })
  public ResponseEntity<UserResponse> show(@PathVariable Integer id) {
    return ResponseEntity.ok(userService.show(id));
  }
}
