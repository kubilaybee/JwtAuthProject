package com.auth.JWTAuth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateRequestDTO {
  @Email(message = "Email is not well formatted")
  @NotEmpty(message = "Email is mandatory")
  @NotNull(message = "Email is mandatory")
  private String email;

  @NotEmpty(message = "Password is mandatory")
  @NotNull(message = "Password is mandatory")
  @Size(min = 4, message = "Password should be 8 characters long minimum")
  private String password;
}
