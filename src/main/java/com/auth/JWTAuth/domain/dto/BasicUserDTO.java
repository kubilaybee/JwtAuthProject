package com.auth.JWTAuth.domain.dto;

import com.auth.JWTAuth.constant.AppConstants;
import java.util.Collection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserDTO {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Collection<RoleDTO> roles;
  private AppConstants.UserStatusEnum userStatus;
}
