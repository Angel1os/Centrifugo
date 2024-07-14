

CREATE TABLE IF NOT EXISTS user_role(

    user_id UUID,

    role_id UUID
);

-- Creating foreign key constraint on user and user-role table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'user_role_user_id_fk' AND conrelid = 'user_role'::regclass) THEN
-- Create the constraint
ALTER TABLE user_role
    ADD CONSTRAINT user_role_user_id_fk
        FOREIGN KEY (user_id) REFERENCES "user"
            ON DELETE CASCADE;
END IF;
END $$;

-- Creating foreign key constraint on role and user-role table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'user_role_role_id_fk' AND conrelid = 'user_role'::regclass) THEN
-- Create the constraint
ALTER TABLE user_role
    ADD CONSTRAINT user_role_role_id_fk
        FOREIGN KEY (role_id) REFERENCES ceptra_setup.role
            ON DELETE CASCADE;
END IF;
END $$;

-- Creating index on the USER ROLE TABLE
CREATE UNIQUE INDEX ON user_role(user_id, role_id);