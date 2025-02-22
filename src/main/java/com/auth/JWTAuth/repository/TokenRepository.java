package com.auth.JWTAuth.repository;

import com.auth.JWTAuth.domain.entity.Token;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TokenRepository
    extends JpaRepository<Token, UUID>, JpaSpecificationExecutor<Token> {
  Optional<Token> findByToken(String token);

  @EntityGraph(attributePaths = {"user"})
  List<Token> findAllByValidatedAtIsNullAndIsDeletedFalse();

  @EntityGraph(attributePaths = {"user"})
  Optional<Token> findByUserId(UUID userId);
}
