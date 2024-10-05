CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    security_question VARCHAR(255) NOT NULL,
    security_answer VARCHAR(255) NOT NULL
)

CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    sender INT REFERENCES users(id) ON DELETE CASCADE,
    receiver INT REFERENCES users(id) ON DELETE CASCADE,
    message_content TEXT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'SENT',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE avatars (
    id SERIAL PRIMARY KEY,
    image BYTEA NOT NULL,
    source VARCHAR(255) NOT NULL,
    fur_color VARCHAR(63) NOT NULL,
    eye_color VARCHAR(63) NOT NULL,
    pattern VARCHAR(63) NOT NULL,
    breed VARCHAR(63) NOT NULL,
    age VarCHAR(63) NOT NULL
)