package com.auth.JWTAuth.exception.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonErrorResponse {
  private List<CommonError> errors;
}
