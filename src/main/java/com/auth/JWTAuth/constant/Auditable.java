package com.auth.JWTAuth.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

  @Schema(hidden = true)
  @CreatedBy
  U createdBy;

  @CreatedDate Instant createdAt;

  @Schema(hidden = true)
  @LastModifiedBy
  U updatedBy;

  @Schema(hidden = true)
  @LastModifiedDate
  Instant updatedAt;
}
