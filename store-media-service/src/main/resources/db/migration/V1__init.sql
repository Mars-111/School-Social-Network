

CREATE TABLE files (
        id SERIAL PRIMARY KEY,
        key VARCHAR(100) UNIQUE NOT NULL,
        owner_id BIGINT,
        type VARCHAR(20),
        special_id BIGINT
);
