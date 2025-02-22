package com.auth.JWTAuth.db.migration;

import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class V1_4__create_super_user extends BaseJavaMigration {
  @Value("${application.superUser.uuid}")
  private String superUserUuid;

  @Value("${application.superUser.email}")
  private String superUserEmail;

  @Override
  public void migrate(Context context) throws Exception {
    log.info("V1_4__create_super_user migrate starts");
    try (Statement insertMember = context.getConnection().createStatement()) {
      String sql =
          "insert into public.basic_user (id,first_name,last_name,email,"
              + "user_status,is_deleted,created_by,updated_by)"
              + " values ("
              + "'"
              + superUserUuid
              + "'"
              + ",'super','super',"
              + "'"
              + superUserEmail
              + "'"
              + ",'ACTIVE',false,'system','system');";
      insertMember.execute(sql);
    }
    try (Statement insertMember = context.getConnection().createStatement()) {
      String sql =
          "insert into public.basic_users_roles (user_id,role_id)"
              + " values((SELECT m.id FROM public.basic_user m WHERE m.email="
              + "'"
              + superUserEmail
              + "'"
              + "), "
              + "(SELECT r.id FROM public.basic_role r WHERE r.name = 'SUPER'));";
      insertMember.execute(sql);

    } catch (Exception e) {
      e.printStackTrace();
      log.error("Error occurred at V1_4__create_super_user.migrate : " + e.getMessage());
    }
    log.info("V1_4__create_super_user migrate ends");
  }
}
