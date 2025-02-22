package com.auth.JWTAuth.controller;

import com.auth.JWTAuth.domain.dto.BasicUserDTO;
import com.auth.JWTAuth.exception.types.DataNotFoundException;
import com.auth.JWTAuth.service.UserDetailsServiceImpl;
import com.auth.JWTAuth.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {
  @Autowired private UserDetailsServiceImpl userDetailsService;

  public BasicUserDTO getLoggedInUser() throws DataNotFoundException {
    String email = ContextUtil.getUsernameFromContext();
    return userDetailsService.findByEmail(email);
  }
}
