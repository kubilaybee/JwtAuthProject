package com.auth.JWTAuth.service;

import com.auth.JWTAuth.constant.AppConstants;
import com.auth.JWTAuth.domain.dto.request.AuthenticateRequestDTO;
import com.auth.JWTAuth.domain.dto.request.ForgotPasswordRequestDTO;
import com.auth.JWTAuth.domain.dto.request.RegistrationRequestDTO;
import com.auth.JWTAuth.domain.dto.request.UserConfirmRequestDTO;
import com.auth.JWTAuth.domain.entity.Role;
import com.auth.JWTAuth.domain.entity.Token;
import com.auth.JWTAuth.domain.entity.User;
import com.auth.JWTAuth.exception.types.BadRequestException;
import com.auth.JWTAuth.repository.RoleRepository;
import com.auth.JWTAuth.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Value("${application.mailing.frontend.activation-url}")
  private String activationUrl;

  public void registration(RegistrationRequestDTO request) throws Exception {
    registrationValidation(request);

    Optional<Role> userRole = roleRepository.findByName(request.getRole());

    var user =
        User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .userStatus(AppConstants.UserStatusEnum.PENDING)
            .roles(List.of(userRole.get()))
            .build();

    userRepository.saveAndFlush(user);
    sendValidationEmail(user);
  }

  private void sendValidationEmail(User user) throws MessagingException {
    var newToken = tokenService.generateAndSaveActivationToken(user);
    emailService.sendEmail(
        user.getEmail(),
        user.getFullName(),
        AppConstants.EmailTemplateName.ACTIVATE_ACCOUNT,
        activationUrl,
        newToken,
        "user.activation.message");
  }

  public void activateAccount(UserConfirmRequestDTO requestDTO) throws Exception {
    Token savedToken = tokenService.findByToken(requestDTO.getToken());

    userActivateValidation(savedToken.getUser());

    savedToken.getUser().setUserStatus(AppConstants.UserStatusEnum.ACTIVE);
    savedToken.getUser().setPassword(passwordEncoder.encode(requestDTO.getPassword()));
    userRepository.saveAndFlush(savedToken.getUser());
    savedToken.setValidatedAt(Instant.now());
    tokenService.saveAndFlush(savedToken);
  }

  public Map<String, Object> authenticate(AuthenticateRequestDTO request) throws Exception {
    userAuthenticateValidation(request);
    var auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    var claims = new HashMap<String, Object>();
    var user = ((User) auth.getPrincipal());
    claims.put("fullName", user.getFullName());
    var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
    Map<String, Object> response = new HashMap<>();
    response.put("token", jwtToken);
    return response;
  }

  private void userAuthenticateValidation(AuthenticateRequestDTO request) throws Exception {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(
                () ->
                    new BadRequestException(request.getEmail(), "email", "user.not.found.message"));
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadRequestException(
          request.getEmail(), request.getEmail(), "user.invalid.authentication.message");
    }
  }

  private void userActivateValidation(User user) throws Exception {
    if (!AppConstants.UserStatusEnum.PENDING.getValue().equals(user.getUserStatus().getValue())) {
      throw new BadRequestException("token", "token", "token.invalid.message");
    }
  }

  private void registrationValidation(RegistrationRequestDTO registrationRequestDTO)
      throws Exception {
    if (!AppConstants.UserRoles.isValidRole(registrationRequestDTO.getRole())
        || !AppConstants.UserRoles.USER.getValue().equals(registrationRequestDTO.getRole())) {
      throw new BadRequestException(
          registrationRequestDTO.getRole(), "Role", "user.invalid.role.exception.message");
    }
    if (userRepository.findByEmail(registrationRequestDTO.getEmail()).isPresent()) {
      throw new BadRequestException(
          registrationRequestDTO.getEmail(), "email", "user.already.exception.message");
    }
  }

  public void forgotPassword(ForgotPasswordRequestDTO email) throws Exception {
    Optional<User> user = userRepository.findByEmail(email.getEmail());
    if (user.isEmpty()) {
      throw new BadRequestException(email.getEmail(), "email", "user.not.found.message");
    }
    user.get().setPassword(null);
    user.get().setUserStatus(AppConstants.UserStatusEnum.PENDING);
    sendValidationEmail(user.get());
    userRepository.saveAndFlush(user.get());
  }
}
