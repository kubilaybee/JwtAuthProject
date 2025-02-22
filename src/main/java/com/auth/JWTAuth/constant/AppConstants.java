package com.auth.JWTAuth.constant;

import java.util.Locale;

public class AppConstants {
  public static final Locale locale = new Locale("en", "US");
  public static final int INVITATION_TOKEN_EXPIRATION_DURATION = 10; // minute
  public static final String ACTIVATION_TOKEN_VARIABLES = "0123456789";
  public static final String SWAGGER_TITLE = "JWT Auth API";
  public static final String SWAGGER_DESCRIPTION = "JWT Auth API List Description";
  public static final String SWAGGER_CONTACT_MAIL = "contact@swagger.com";
  public static final String SWAGGER_CONTACT_NAME = "Contact Name";
  public static final String SWAGGER_CONTACT_URL = "contact.com";
  public static final String SWAGGER_API_VERSION = "1.0.0";
  public static final String SWAGGER_SERVER_DESCRIPTION = "Server Description";
  public static final int ACTIVATION_TOKEN_SIZE = 6;

  public enum UserStatusEnum {
    ACTIVE("ACTIVE"),
    DEACTIVATED("DEACTIVATED"),
    PENDING("PENDING"),
    EXPIRED("EXPIRED");

    private final String code;

    UserStatusEnum(String code) {
      this.code = code;
    }

    public String getValue() {
      return code;
    }
  }

  public enum UserRoles {
    SUPER("SUPER"),
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    UserRoles(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static boolean isValidRole(String value) {
      for (UserRoles role : values()) {
        if (role.value.equalsIgnoreCase(value)) {
          return true;
        }
      }
      return false;
    }
  }

  public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");
    private final String name;

    EmailTemplateName(String name) {
      this.name = name;
    }

    public String getValue() {
      return name;
    }
  }
}
