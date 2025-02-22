package com.auth.JWTAuth.service;

import com.auth.JWTAuth.domain.entity.Role;
import com.auth.JWTAuth.exception.types.DataNotFoundException;
import com.auth.JWTAuth.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleService {
  private final RoleRepository roleRepository;

  public Role findByName(String role) throws DataNotFoundException {
    return roleRepository
        .findByName(role)
        .orElseThrow(
            () -> new DataNotFoundException(role, "Role", "user.invalid.role.exception.message"));
  }

  public List<Role> findAll() {
    return roleRepository.findAll();
  }
}
