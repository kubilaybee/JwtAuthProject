package com.auth.JWTAuth.exception.types;

public class DataNotFoundException extends BaseException {
  public DataNotFoundException(String placeholder, String source, String messageId) {
    super(placeholder, source, messageId);
  }
}
