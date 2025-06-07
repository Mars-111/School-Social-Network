CREATE TABLE files (
                       id BIGSERIAL PRIMARY KEY,
                       key TEXT NOT NULL,
                       owner_id BIGINT NOT NULL,
                       extension TEXT NOT NULL,        -- новое поле
                       size BIGINT NOT NULL,
                       status TEXT NOT NULL,
                       filename TEXT NOT NULL,  -- новое поле
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX ux_files_key ON files(key);
