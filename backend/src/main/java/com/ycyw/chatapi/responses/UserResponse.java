package com.ycyw.chatapi.responses;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class UserResponse {
  private Integer id;
  private String name;
  private String email;
  private Date createdAt;
  private Date updatedAt;
}
