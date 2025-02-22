package com.auth.JWTAuth.repository;

import com.auth.JWTAuth.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
  @EntityGraph(attributePaths = {"roles"})
  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = {"roles"})
  @Override
  Optional<User> findById(UUID id);
}
