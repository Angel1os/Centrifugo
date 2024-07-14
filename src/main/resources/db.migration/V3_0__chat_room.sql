

CREATE TABLE IF NOT EXISTS chat_room(

    id UUID PRIMARY KEY,

    name VARCHAR(60) NOT NULL,

    updated_at TIMESTAMPTZ,

    updated_by UUID,

    created_at TIMESTAMPTZ NOT NULL,

    created_by UUID NOT NULL,

    FOREIGN KEY (created_by) REFERENCES "user"(id)

    );


-- Creating index on the CHAT ROOM TABLE
CREATE UNIQUE INDEX ON chat_room(id, name);