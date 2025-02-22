package com.auth.JWTAuth.exception.types;

public class ConflictException extends BaseException {
  public ConflictException(String placeholder, String source, String messageId) {
    super(placeholder, source, messageId);
  }
}
