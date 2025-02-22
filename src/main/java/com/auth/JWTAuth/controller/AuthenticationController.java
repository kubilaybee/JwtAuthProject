package com.auth.JWTAuth.controller;

import static com.auth.JWTAuth.constant.AppConstants.locale;

import com.auth.JWTAuth.domain.dto.request.AuthenticateRequestDTO;
import com.auth.JWTAuth.domain.dto.request.ForgotPasswordRequestDTO;
import com.auth.JWTAuth.domain.dto.request.RegistrationRequestDTO;
import com.auth.JWTAuth.domain.dto.request.UserConfirmRequestDTO;
import com.auth.JWTAuth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthenticationController", description = "Authentication Operations")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController extends BaseController {
  @Autowired private AuthenticationService authService;
  @Autowired private MessageSource messageSource;

  @GetMapping("/register")
  @Operation(summary = "User Register")
  public ResponseEntity<?> registerUser(
      @Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) throws Exception {
    authService.registration(registrationRequestDTO);
    String responseMessage = messageSource.getMessage("user.register.message", null, locale);
    return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
  }

  @GetMapping("/confirm")
  @Operation(summary = "User Confirm")
  public ResponseEntity<?> confirmUser(@Valid @RequestBody UserConfirmRequestDTO requestDTO)
      throws Exception {

    authService.activateAccount(requestDTO);
    String responseMessage = messageSource.getMessage("user.activation.message", null, locale);
    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
  }

  @GetMapping("/login")
  @Operation(summary = "User Authenticate")
  public ResponseEntity<Map<String, Object>> loginUser(
      @Valid @RequestBody AuthenticateRequestDTO authRequest) throws Exception {
    Map<String, Object> response = authService.authenticate(authRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/forgot-password")
  @Operation(summary = "User Forgot Password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO email)
      throws Exception {
    authService.forgotPassword(email);
    String responseMessage = messageSource.getMessage("user.forgot.password.message", null, locale);
    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
  }
}
