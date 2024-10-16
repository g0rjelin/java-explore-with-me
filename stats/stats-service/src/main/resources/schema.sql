CREATE TABLE IF NOT EXISTS apps (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL
    );

CREATE TABLE IF NOT EXISTS uris (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL
    );

CREATE TABLE IF NOT EXISTS endpoint_hits (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    app_id BIGINT REFERENCES apps(id),
    uri_id BIGINT REFERENCES uris(id),
    ip VARCHAR(15) NOT NULL,
    created TIMESTAMP NOT NULL
    );