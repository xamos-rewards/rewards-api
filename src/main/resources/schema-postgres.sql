DROP TABLE IF EXISTS rewards CASCADE;
CREATE TABLE rewards (
    username VARCHAR(383) PRIMARY KEY, -- 383 characters comes from Auth0 max lengths for user ids. 255 characters for user id and 128 characters for identity provider
    points INT NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS applications CASCADE;
CREATE TABLE applications (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    client_id VARCHAR(32) UNIQUE NOT NULL -- Auth0 client_ids are 32 characters long
);
