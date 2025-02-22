package com.auth.JWTAuth.util.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidEmailValidator.class)
@Documented
public @interface ValidEmail {

  String message() default "{validation.constraints.ValidEmail.format.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (StringUtils.isBlank(value)) {
      return false;
    }

    final Pattern pattern = Pattern.compile("^(.+)@(.+)$");

    return pattern.matcher(value).matches();
  }
}
