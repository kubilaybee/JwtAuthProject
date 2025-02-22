package com.auth.JWTAuth.exception.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonError {
  private int status;
  private String source;
  private String detail;
}
