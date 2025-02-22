package com.auth.JWTAuth.exception.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends Exception {
  private String source;
  private String messageId;
  private Object[] placeholders;

  public BaseException(String placeholder, String source, String messageId) {
    super();
    this.source = source;
    this.messageId = messageId;
    this.placeholders = new Object[] {placeholder};
  }
}
