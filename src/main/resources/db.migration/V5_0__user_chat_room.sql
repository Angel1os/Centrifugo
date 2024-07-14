

CREATE TABLE IF NOT EXISTS user_chat_room(

    user_id UUID,

    chat_room_id UUID
);

-- Creating foreign key constraint on user and user-chat-room table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'user_chat_room_user_id_fk' AND conrelid = 'user_chat_room'::regclass) THEN
-- Create the constraint
ALTER TABLE user_chat_room
    ADD CONSTRAINT user_chat_room_user_id_fk
        FOREIGN KEY (user_id) REFERENCES "user"
            ON DELETE CASCADE;
END IF;
END $$;

-- Creating foreign key constraint on chat_room and user-chat-room table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'user_chat_room_chat_room_id_fk' AND conrelid = 'user_chat_room'::regclass) THEN
-- Create the constraint
ALTER TABLE user_chat_room
    ADD CONSTRAINT user_chat_room_chat_room_id_fk
        FOREIGN KEY (chat_room_id) REFERENCES chat_room
            ON DELETE CASCADE;
END IF;
END $$;

-- Creating index on the USER ROLE TABLE
CREATE UNIQUE INDEX ON user_chat_room(user_id, chat_room_id);