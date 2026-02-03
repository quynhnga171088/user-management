-- Create Users Table matching the JPA Entity
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create User Roles Table (ElementCollection)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert Admin User (Password: admin123)
-- Hash generated using BCrypt
INSERT INTO users (name, email, password, create_date, active) VALUES ('Admin', 'admin@example.com', '$2a$10$kUkrVk/1n/ELNOA2GAK2SupfncJSxulJKjovV9rlTV.oN7vKPvEAi', CURRENT_TIMESTAMP, true) ON CONFLICT (email) DO NOTHING;
INSERT INTO user_roles (user_id, role) VALUES ((SELECT id FROM users WHERE email='admin@example.com'), 'ADMIN') ON CONFLICT DO NOTHING;
