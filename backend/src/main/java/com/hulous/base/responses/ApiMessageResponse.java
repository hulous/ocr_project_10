package com.hulous.base.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Schema(description = "Standard error or information message.")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class ApiMessageResponse {
  @Schema(example = "Invalid email or password")
  private String message;
}
