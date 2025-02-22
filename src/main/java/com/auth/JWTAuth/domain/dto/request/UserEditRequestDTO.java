package com.auth.JWTAuth.domain.dto.request;

import com.auth.JWTAuth.util.validation.ValidFirstName;
import com.auth.JWTAuth.util.validation.ValidLastName;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditRequestDTO {
  @NotNull private UUID id;
  @ValidFirstName private String firstName;
  @ValidLastName private String lastName;
}
