-- R__001: Seed admin user
-- Password: Admin@12345 (BCrypt hash with strength 12)

INSERT INTO users (id, username, email, password_hash, full_name, role, status, failed_login_attempts, created_at, updated_at)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'admin',
    'admin@hospital.com',
    '$2a$12$6nyJ0Hd9CyiGoiBw9e.1BeB8rf1G4OODuZuG.hyt7sPX/nMsvFN6W',
    'System Administrator',
    'ADMINISTRATOR',
    'ACTIVE',
    0,
    NOW(),
    NOW()
) ON CONFLICT (username) DO UPDATE SET password_hash = EXCLUDED.password_hash;
