package com.auth.JWTAuth.domain.dto.request;

import com.auth.JWTAuth.util.validation.ValidEmail;
import com.auth.JWTAuth.util.validation.ValidFirstName;
import com.auth.JWTAuth.util.validation.ValidLastName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequestDTO {
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @ValidFirstName
  private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @ValidLastName
  private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @ValidEmail
  private String email;

  private String role;
}
