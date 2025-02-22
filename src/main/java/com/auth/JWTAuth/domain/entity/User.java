package com.auth.JWTAuth.domain.entity;

import com.auth.JWTAuth.constant.AppConstants;
import com.auth.JWTAuth.constant.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "basic_user")
public class User extends Auditable<String> implements UserDetails, Serializable {
  @Serial private static final long serialVersionUID = 1L;
  @Id @GeneratedValue private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private boolean isDeleted;

  @Enumerated(EnumType.STRING)
  AppConstants.UserStatusEnum userStatus;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @JoinTable(
      name = "basic_users_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  protected Collection<Role> roles = new HashSet<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return this.roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList();
  }

  @Override
  public String getUsername() {
    return this.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }
}
