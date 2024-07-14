

CREATE TABLE IF NOT EXISTS message(

    id UUID PRIMARY KEY,

    chat_room_id UUID NOT NULL,

    sender_id VARCHAR(265) NOT NULL,

    content UUID NOT NULL,

    updated_at TIMESTAMPTZ,

    updated_by UUID,

    created_at TIMESTAMPTZ NOT NULL,

    created_by UUID NOT NULL,

    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id),
    FOREIGN KEY (sender_id) REFERENCES "user"(id)

    );


-- Creating index on the MESSAGE TABLE
CREATE UNIQUE INDEX ON message(id, content);