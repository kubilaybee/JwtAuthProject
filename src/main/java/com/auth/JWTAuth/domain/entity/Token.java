package com.auth.JWTAuth.domain.entity;

import static com.auth.JWTAuth.constant.AppConstants.INVITATION_TOKEN_EXPIRATION_DURATION;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "basic_token")
public class Token implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true)
  private String token;

  private Instant expirationDate;
  private Instant validatedAt;
  private boolean isDeleted;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public Token(String token) {
    this.token = token;
    this.expirationDate = calculateExpiryDate();
  }

  private Instant calculateExpiryDate() {
    return Instant.now().plus(Duration.ofMinutes(INVITATION_TOKEN_EXPIRATION_DURATION));
  }
}
