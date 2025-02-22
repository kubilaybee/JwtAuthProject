package com.auth.JWTAuth.exception;

import com.auth.JWTAuth.exception.domain.CommonError;
import com.auth.JWTAuth.exception.domain.CommonErrorResponse;
import com.auth.JWTAuth.exception.types.BadRequestException;
import com.auth.JWTAuth.exception.types.BaseException;
import com.auth.JWTAuth.exception.types.DataNotFoundException;
import com.auth.JWTAuth.exception.types.PermissionException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

  @Autowired private Environment environment;

  @Autowired private MessageSource messageSource;

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonErrorResponse handleBadRequestException(BadRequestException exception) {
    log.error("Bad Request Exception: ", exception);
    return buildCommonErrorResponse(HttpStatus.BAD_REQUEST, exception);
  }

  @ExceptionHandler(PermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public CommonErrorResponse handlePermissionException(PermissionException exception) {
    log.error("Permission Exception: ", exception);
    return buildCommonErrorResponse(HttpStatus.FORBIDDEN, exception);
  }

  @ExceptionHandler(DataNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public CommonErrorResponse handleDataNotFoundException(DataNotFoundException exception) {
    log.error("Data Not Found Exception: ", exception);
    return buildCommonErrorResponse(HttpStatus.NOT_FOUND, exception);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<CommonError> handleMethodArgumentException(
      MethodArgumentNotValidException exception) {

    return exception.getBindingResult().getFieldErrors().stream()
        .map(
            error -> {
              CommonError commonError = new CommonError();
              commonError.setStatus(HttpStatus.BAD_REQUEST.value());
              commonError.setSource(error.getField());
              commonError.setDetail(error.getDefaultMessage());
              return commonError;
            })
        .toList();
  }

  private CommonErrorResponse buildCommonErrorResponse(HttpStatus status, String message) {
    CommonError commonError = new CommonError();
    commonError.setStatus(status.value());
    commonError.setDetail(message);

    CommonErrorResponse commonErrorResponse = new CommonErrorResponse();
    commonErrorResponse.setErrors(Collections.singletonList(commonError));

    return commonErrorResponse;
  }

  private CommonErrorResponse buildCommonErrorResponse(HttpStatus status, BaseException exception) {
    CommonError commonError = new CommonError();
    commonError.setStatus(status.value());
    commonError.setSource(exception.getSource());
    commonError.setDetail(
        messageSource.getMessage(
            exception.getMessageId(), exception.getPlaceholders(), Locale.ENGLISH));

    CommonErrorResponse commonErrorResponse = new CommonErrorResponse();
    commonErrorResponse.setErrors(Collections.singletonList(commonError));

    return commonErrorResponse;
  }

  private CommonErrorResponse returnCommonErrorResponse(
      HttpStatus status, String code, String message) {
    CommonError commonError = new CommonError();
    commonError.setStatus(status.value());
    commonError.setSource(code);
    commonError.setDetail(message);

    CommonErrorResponse commonErrorResponse = new CommonErrorResponse();
    commonErrorResponse.setErrors(Collections.singletonList(commonError));

    return commonErrorResponse;
  }
}
