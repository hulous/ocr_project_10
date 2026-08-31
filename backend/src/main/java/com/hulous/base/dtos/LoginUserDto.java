package com.hulous.base.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString(exclude = "password")
public class LoginUserDto {
  private String email;
  private String password;
}
