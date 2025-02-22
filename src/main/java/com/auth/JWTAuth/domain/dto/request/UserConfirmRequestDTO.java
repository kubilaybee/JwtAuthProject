package com.auth.JWTAuth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserConfirmRequestDTO {

  @NotBlank
  @Size(min = 6, max = 6, message = "token.invalid.message")
  private String token;

  @NotBlank private String password;
}
