CREATE DATABASE ignite;

CREATE TABLE profile
(
    id bigint PRIMARY KEY,
    name text NOT NULL
);

CREATE TABLE relation
(
    id uuid PRIMARY KEY,
    base bigint NOT NULL,
    reference bigint NOT NULL
);