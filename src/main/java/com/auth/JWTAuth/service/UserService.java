package com.auth.JWTAuth.service;

import com.auth.JWTAuth.constant.AppConstants;
import com.auth.JWTAuth.domain.dto.BasicUserDTO;
import com.auth.JWTAuth.domain.dto.request.UserEditRequestDTO;
import com.auth.JWTAuth.domain.dto.request.UserRoleUpdateRequestDTO;
import com.auth.JWTAuth.domain.entity.Role;
import com.auth.JWTAuth.domain.entity.Token;
import com.auth.JWTAuth.domain.entity.User;
import com.auth.JWTAuth.exception.types.BadRequestException;
import com.auth.JWTAuth.exception.types.DataNotFoundException;
import com.auth.JWTAuth.repository.UserRepository;
import com.auth.JWTAuth.repository.specification.UserSpecification;
import com.auth.JWTAuth.util.ModelMapperUtils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
  @Value("${application.superUser.email}")
  private String superUserEmail;

  @Value("${application.superUser.password}")
  private String superUserPassword;

  private final UserRepository userRepository;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final UserSpecification userSpecification;
  private final RoleService roleService;

  @Transactional
  public void updateTokenAndUserStatus() {
    List<Token> expiredTokenList = tokenService.expiredTokenList();
    expiredTokenList.forEach(
        token -> {
          token.setDeleted(true);
          if (Objects.nonNull(token.getUser())) {
            token.getUser().setUserStatus(AppConstants.UserStatusEnum.EXPIRED);
          }
        });
    tokenService.saveAllAndFlush(expiredTokenList);
  }

  @PostConstruct
  public void setSuperUserPassword() {
    User superUser = userRepository.findByEmail(superUserEmail).get();
    if (Objects.isNull(superUser.getPassword())) {
      superUser.setPassword(passwordEncoder.encode(superUserPassword));
      userRepository.saveAndFlush(superUser);
    }
  }

  public Page<BasicUserDTO> loadUsers(
      String firstName, String lastName, String email, Pageable pageable) throws Exception {

    return userSpecification.getUsers(firstName, lastName, email, pageable);
  }

  public BasicUserDTO findById(UUID id) throws Exception {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new DataNotFoundException("User", "User", "user.not.found.message"));
    return ModelMapperUtils.map(user, BasicUserDTO.class);
  }

  public BasicUserDTO editUser(UserEditRequestDTO userEditRequest) throws Exception {
    User user = userRepository.findById(userEditRequest.getId()).get();
    ModelMapperUtils.map(userEditRequest, user);
    userRepository.saveAndFlush(user);
    return ModelMapperUtils.map(user, BasicUserDTO.class);
  }

  public BasicUserDTO changeRole(UserRoleUpdateRequestDTO userRole) throws Exception {
    if (!AppConstants.UserRoles.isValidRole(userRole.getRole())
        || AppConstants.UserRoles.SUPER.getValue().equals(userRole.getRole())) {
      throw new BadRequestException(
          userRole.getRole(), "Role", "user.invalid.role.exception.message");
    }
    User user =
        userRepository
            .findById(userRole.getId())
            .orElseThrow(() -> new DataNotFoundException("user", "user", "user.not.found.message"));
    user.setRoles(findToRole(userRole.getRole()));
    userRepository.saveAndFlush(user);
    return ModelMapperUtils.map(user, BasicUserDTO.class);
  }

  public Set<Role> findToRole(String role) {
    List<Role> roles = roleService.findAll();
    Set<Role> memberRoles = new HashSet<>();
    for (Role roleFromDb : roles) {
      if (roleFromDb.getName().equalsIgnoreCase(role)) {
        memberRoles.add(roleFromDb);
      }
    }

    return memberRoles;
  }
}
