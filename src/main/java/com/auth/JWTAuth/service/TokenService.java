package com.auth.JWTAuth.service;

import static com.auth.JWTAuth.constant.AppConstants.ACTIVATION_TOKEN_SIZE;
import static com.auth.JWTAuth.constant.AppConstants.ACTIVATION_TOKEN_VARIABLES;
import static com.auth.JWTAuth.constant.AppConstants.INVITATION_TOKEN_EXPIRATION_DURATION;

import com.auth.JWTAuth.domain.entity.Token;
import com.auth.JWTAuth.domain.entity.User;
import com.auth.JWTAuth.exception.types.BadRequestException;
import com.auth.JWTAuth.repository.TokenRepository;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {
  private final TokenRepository tokenRepository;

  public String generateAndSaveActivationToken(User user) {
    Optional<Token> oldToken = findByUserId(user.getId());

    String generatedToken = generateActivationToken();
    if (oldToken.isEmpty()) {
      var token =
          Token.builder()
              .token(generatedToken)
              .expirationDate(
                  Instant.now().plus(Duration.ofMinutes(INVITATION_TOKEN_EXPIRATION_DURATION)))
              .user(user)
              .build();
      tokenRepository.saveAndFlush(token);
      return generatedToken;
    } else {
      oldToken.get().setToken(generatedToken);
      oldToken
          .get()
          .setExpirationDate(
              Instant.now().plus(Duration.ofMinutes(INVITATION_TOKEN_EXPIRATION_DURATION)));
      oldToken.get().setValidatedAt(null);
      oldToken.get().setDeleted(false);
      tokenRepository.saveAndFlush(oldToken.get());
      return generatedToken;
    }
  }

  private String generateActivationToken() {
    StringBuilder codeBuilder = new StringBuilder();

    SecureRandom secureRandom = new SecureRandom();

    for (int i = 0; i < ACTIVATION_TOKEN_SIZE; i++) {
      int randomIndex = secureRandom.nextInt(ACTIVATION_TOKEN_VARIABLES.length());
      codeBuilder.append(ACTIVATION_TOKEN_VARIABLES.charAt(randomIndex));
    }

    return codeBuilder.toString();
  }

  public Token findByToken(String token) throws BadRequestException {
    return tokenRepository
        .findByToken(token)
        .orElseThrow(() -> new RuntimeException("token.invalid.message"));
  }

  private Optional<Token> findByUserId(UUID userId) {
    return tokenRepository.findByUserId(userId);
  }

  public void saveAndFlush(Token token) {
    tokenRepository.saveAndFlush(token);
  }

  public void saveAllAndFlush(List<Token> tokenList) {
    tokenRepository.saveAllAndFlush(tokenList);
  }

  public List<Token> expiredTokenList() {
    return tokenRepository.findAllByValidatedAtIsNullAndIsDeletedFalse();
  }
}
