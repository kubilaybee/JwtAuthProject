package com.auth.JWTAuth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO {
  @Email(message = "Email is not well formatted")
  @NotEmpty(message = "Email is mandatory")
  @NotNull(message = "Email is mandatory")
  private String email;
}
