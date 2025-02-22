ALTER TABLE if exists public.basic_users_roles drop constraint if exists fk_role;
ALTER TABLE if exists public.basic_users_roles ADD CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES public.basic_role(id);

ALTER TABLE if exists public.basic_users_roles drop constraint if exists fk_user;
ALTER TABLE if exists public.basic_users_roles ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.basic_user(id);

ALTER TABLE if exists public.basic_token drop constraint if exists fk_user_token;
ALTER TABLE if exists public.basic_token ADD CONSTRAINT fk_user_token FOREIGN KEY (user_id) REFERENCES public.basic_user(id);
