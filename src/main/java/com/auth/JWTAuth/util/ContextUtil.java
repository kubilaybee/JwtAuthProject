package com.auth.JWTAuth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public final class ContextUtil {
  private ContextUtil() {}

  public static String getUsernameFromContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && authentication.getPrincipal() instanceof UserDetails) {
      return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
    return null;
  }
}
