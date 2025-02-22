--CREATE TABLE "books" (
--    "isbn" text NOT NULL,
--    "title" text,
--    CONSTRAINT "books_pkey" PRIMARY KEY ("isbn")
--);

CREATE TABLE if not exists public.basic_role (
    id uuid NOT NULL,
    name character varying(255),
    primary key (id)
);

CREATE TABLE if not exists public.basic_users_roles (
    user_id uuid NOT NULL,
    role_id uuid NOT NULL
);

CREATE TABLE if not exists public.basic_user (
    id uuid NOT NULL,
    first_name character varying(100),
    last_name character varying(100),
    email character varying(150),
    password character varying(255),
    user_status varchar(20),
    is_deleted boolean NOT NULL,
    created_at timestamptz, created_by varchar(255), updated_at timestamptz, updated_by varchar(255),
    primary key (id)
);

CREATE TABLE if not exists public.basic_token (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token character varying(255),
    expiration_date timestamptz, validated_at timestamptz,
    is_deleted boolean NOT NULL
);