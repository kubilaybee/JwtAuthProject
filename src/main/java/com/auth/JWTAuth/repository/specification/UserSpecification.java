package com.auth.JWTAuth.repository.specification;

import com.auth.JWTAuth.domain.dto.BasicUserDTO;
import com.auth.JWTAuth.domain.entity.User;
import com.auth.JWTAuth.repository.UserRepository;
import com.auth.JWTAuth.util.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

  @Autowired private UserRepository userRepository;

  public Page<BasicUserDTO> getUsers(
      String firstName, String lastName, String email, Pageable pageable) {
    Specification<User> spec = Specification.where(null);

    if (firstName != null && !firstName.isEmpty()) {
      spec = spec.and(hasFirstNameIgnoreCase(firstName));
    }

    if (lastName != null && !lastName.isEmpty()) {
      spec = spec.and(hasLastNameIgnoreCase(lastName));
    }

    if (email != null && !email.isEmpty()) {
      spec = spec.and(emailContains(email));
    }

    spec = spec.and(isNotDeleted());

    Page<User> users = userRepository.findAll(spec, pageable);

    Page<BasicUserDTO> userDTOs = ModelMapperUtils.mapPage(users, BasicUserDTO.class);

    return userDTOs;
  }

  private Specification<User> hasFirstNameIgnoreCase(String firstName) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
  }

  private Specification<User> hasLastNameIgnoreCase(String lastName) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
  }

  private Specification<User> emailContains(String email) {
    return (root, query, cb) -> cb.like(root.get("email"), "%" + email + "%");
  }

  private Specification<User> isNotDeleted() {
    return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
  }
}
