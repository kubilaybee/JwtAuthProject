package com.auth.JWTAuth.exception.types;

public class BadRequestException extends BaseException {
  public BadRequestException(String placeholder, String source, String messageId) {
    super(placeholder, source, messageId);
  }
}
