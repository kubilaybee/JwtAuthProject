package com.auth.JWTAuth.service;

import com.auth.JWTAuth.domain.dto.BasicUserDTO;
import com.auth.JWTAuth.domain.entity.User;
import com.auth.JWTAuth.exception.types.DataNotFoundException;
import com.auth.JWTAuth.repository.UserRepository;
import com.auth.JWTAuth.util.ModelMapperUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("user.not.found.message"));
  }

  public BasicUserDTO findByEmail(String email) throws DataNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      return ModelMapperUtils.map(user.get(), BasicUserDTO.class);
    } else {
      throw new DataNotFoundException("User", "User", "user.not.found.message");
    }
  }
}
