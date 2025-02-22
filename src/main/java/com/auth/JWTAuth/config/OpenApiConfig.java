package com.auth.JWTAuth.config;

import static com.auth.JWTAuth.constant.AppConstants.SWAGGER_SERVER_DESCRIPTION;

import com.auth.JWTAuth.constant.AppConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {
  @Value("${swagger.api}")
  String environment;

  @Bean
  public OpenAPI baseOpenAPI() {
    Contact contact = new Contact();
    contact.setEmail(AppConstants.SWAGGER_CONTACT_MAIL);
    contact.setName(AppConstants.SWAGGER_CONTACT_NAME);
    contact.setUrl(AppConstants.SWAGGER_CONTACT_URL);

    Server server = new Server();
    server.setUrl(environment);
    server.setDescription(SWAGGER_SERVER_DESCRIPTION);
    List<Server> serverList = new ArrayList<>();
    serverList.add(server);

    Info info =
        new Info()
            .title(AppConstants.SWAGGER_TITLE)
            .contact(contact)
            .version(AppConstants.SWAGGER_API_VERSION)
            .description(AppConstants.SWAGGER_DESCRIPTION);
    return new OpenAPI().info(info).servers(serverList);
  }

  @Bean
  GroupedOpenApi allApis() {
    return GroupedOpenApi.builder().group("All").pathsToMatch("/**").build();
  }

  @Bean
  public GroupedOpenApi authenticationApi() {
    String[] paths = {"/auth/**"};
    return GroupedOpenApi.builder().group("Authentication").pathsToMatch(paths).build();
  }

  @Bean
  public GroupedOpenApi usersApi() {
    String[] paths = {"/users/**"};
    return GroupedOpenApi.builder().group("Users").pathsToMatch(paths).build();
  }
}
