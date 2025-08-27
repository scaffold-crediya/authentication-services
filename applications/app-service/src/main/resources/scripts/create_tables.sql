CREATE TABLE users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    first_name varchar(160) NULL,
    last_name varchar(200) NULL,
    address varchar(255) NULL,
    phone_number varchar(100) NULL,
    email varchar(255) NULL,
    base_salary numeric(10, 2) NULL,
    birth_date date NULL,
    identity_document varchar(20) NULL,
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_identity_document_key UNIQUE (identity_document),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT roles_name_key UNIQUE (name)
);

