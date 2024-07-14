

CREATE TABLE IF NOT EXISTS "user"(

    id UUID PRIMARY KEY,

    username VARCHAR(60) NOT NULL UNIQUE,

    email VARCHAR(60) NOT NULL UNIQUE,

    first_name VARCHAR(60) NOT NULL,

    last_name VARCHAR(60),

    phone_number VARCHAR(15),

    is_enabled BOOLEAN DEFAULT TRUE,

    updated_at TIMESTAMPTZ,

    updated_by UUID,

    created_at TIMESTAMPTZ NOT NULL,

    created_by UUID NOT NULL,

    FOREIGN KEY (company_id) REFERENCES ceptra_setup.company(id)

    );


-- Creating index on the USER TABLE
CREATE UNIQUE INDEX ON "user"(id, username, email);