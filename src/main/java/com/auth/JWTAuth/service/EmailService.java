package com.auth.JWTAuth.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

import com.auth.JWTAuth.constant.AppConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;

  @Async
  public void sendEmail(
      String to,
      String username,
      AppConstants.EmailTemplateName emailTemplate,
      String confirmationUrl,
      String activationCode,
      String subject)
      throws MessagingException {
    String templateName;
    if (emailTemplate == null) {
      templateName = "confirm-email";
    } else {
      templateName = emailTemplate.getValue();
    }
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper =
        new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());
    Map<String, Object> properties = new HashMap<>();
    properties.put("username", username);
    properties.put("confirmationUrl", confirmationUrl);
    properties.put("activation_code", activationCode);

    Context context = new Context();
    context.setVariables(properties);

    helper.setFrom("contact@application.com");
    helper.setTo(to);
    helper.setSubject(subject);

    String template = templateEngine.process(templateName, context);

    helper.setText(template, true);

    mailSender.send(mimeMessage);
  }
}
