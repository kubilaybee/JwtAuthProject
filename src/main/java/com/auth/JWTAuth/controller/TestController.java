package com.auth.JWTAuth.controller;

import com.auth.JWTAuth.domain.dto.BasicUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TestController", description = "Test Operations")
@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController extends BaseController {

  @GetMapping
  @Operation(summary = "[READY] User Register")
  public ResponseEntity<?> test() throws Exception {
    BasicUserDTO userDTO = getLoggedInUser();
    return new ResponseEntity<>(userDTO, HttpStatus.OK);
  }
}
