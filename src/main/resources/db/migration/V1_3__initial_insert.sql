CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
------ BASIC ROLES --------
INSERT INTO public.basic_role (id,name)
SELECT uuid_generate_v4() AS id, 'SUPER' WHERE NOT EXISTS (
  SELECT name FROM public.basic_role WHERE name = 'SUPER'
);

INSERT INTO public.basic_role (id,name)
SELECT uuid_generate_v4() AS id, 'ADMIN' WHERE NOT EXISTS (
  SELECT name FROM public.basic_role WHERE name = 'ADMIN'
);

INSERT INTO public.basic_role (id,name)
SELECT uuid_generate_v4() AS id, 'USER' WHERE NOT EXISTS (
  SELECT name FROM public.basic_role WHERE name = 'USER'
);