package com.auth.JWTAuth.domain.dto.request;

import java.util.UUID;
import lombok.Data;

@Data
public class UserRoleUpdateRequestDTO {
  private UUID id;
  private String role;
}
