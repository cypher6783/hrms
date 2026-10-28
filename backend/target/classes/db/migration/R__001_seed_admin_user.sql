-- R__001: Seed admin user
-- Password: Admin@12345 (BCrypt hash with strength 12)

INSERT INTO users (id, username, email, password_hash, full_name, role, status, failed_login_attempts, created_at, updated_at)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'admin',
    'admin@hospital.com',
    '$2a$12$LJ3m4ks9hL1NsEY8mGfVOeDxPjP0F5lYrX4jKd9cV5nB7hM2kO3qW',
    'System Administrator',
    'ADMINISTRATOR',
    'ACTIVE',
    0,
    NOW(),
    NOW()
) ON CONFLICT (username) DO NOTHING;
