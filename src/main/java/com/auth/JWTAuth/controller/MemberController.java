package com.auth.JWTAuth.controller;

import com.auth.JWTAuth.domain.dto.BasicUserDTO;
import com.auth.JWTAuth.domain.dto.request.UserEditRequestDTO;
import com.auth.JWTAuth.domain.dto.request.UserRoleUpdateRequestDTO;
import com.auth.JWTAuth.exception.types.PermissionException;
import com.auth.JWTAuth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserController", description = "User Operations")
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class MemberController extends BaseController {
  @Autowired private UserService userService;

  @GetMapping("/{page}/{size}")
  @Operation(summary = "Get All Users")
  public ResponseEntity<?> getAllUser(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String email,
      @PathVariable("page") Integer pageNumber,
      @PathVariable("size") Integer size)
      throws Exception {
    Pageable pageable = PageRequest.of(pageNumber, size);
    Page<BasicUserDTO> alarmList = userService.loadUsers(firstName, lastName, email, pageable);
    return new ResponseEntity<>(alarmList, HttpStatus.OK);
  }

  @GetMapping("/{uuid}")
  @Operation(summary = "Get User User By Id")
  public ResponseEntity<?> getUserById(@PathVariable("uuid") UUID uuid) throws Exception {
    return new ResponseEntity<>(userService.findById(uuid), HttpStatus.OK);
  }

  @PutMapping
  @Operation(summary = "Edit User")
  public ResponseEntity<?> editUser(@Valid @RequestBody UserEditRequestDTO userEditRequest)
      throws Exception {
    BasicUserDTO currentUser = getLoggedInUser();
    if (!currentUser.getId().equals(userEditRequest.getId())) {
      throw new PermissionException("User", "User", "user.not.permission.message");
    }
    BasicUserDTO updatedUser = userService.editUser(userEditRequest);
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  @PutMapping("/change-role")
  @Operation(summary = "Change User Role (only superUser)")
  public ResponseEntity<?> changeUserRole(@Valid @RequestBody UserRoleUpdateRequestDTO userRole)
      throws Exception {
    BasicUserDTO updatedUser = userService.changeRole(userRole);
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }
}
