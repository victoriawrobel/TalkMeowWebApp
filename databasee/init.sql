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
                          timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
