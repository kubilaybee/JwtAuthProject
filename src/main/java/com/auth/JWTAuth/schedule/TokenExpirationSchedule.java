package com.auth.JWTAuth.schedule;

import com.auth.JWTAuth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenExpirationSchedule {
  private final UserService userService;

  @Scheduled(cron = "0 0/5 * * * *") // # every 5 minutes
  public void changeStatus() {
    try {
      log.info("Token Expiration Schedule starts");
      userService.updateTokenAndUserStatus();
      log.info("Token Expiration Schedule ends");
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
