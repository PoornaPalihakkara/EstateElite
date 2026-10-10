-- ==========================================================
-- Estate Elite demo seed data
-- All demo accounts use the password:  Password123!
-- ==========================================================

INSERT INTO agencies (id, name, license_number, contact_email, contact_phone, address, status)
SELECT 1, 'Elite Realty Group', 'LIC-2026-001', 'contact@eliterealty.lk', '0112345678', '45 Galle Road, Colombo', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM agencies WHERE id = 1);

INSERT INTO users (id, full_name, email, password, phone, role, agency_id, profile_image, created_at)
SELECT 1, 'Admin User', 'admin@estateelite.lk', '$2b$10$txY5dL0VhJacDM3ZU3ZB/e.tylbysJIM9hI7noKnddf0ohjT2Q3jO', '0770000001', 'ADMIN', NULL, NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 1);

INSERT INTO users (id, full_name, email, password, phone, role, agency_id, profile_image, created_at)
SELECT 2, 'Poorna Seller', 'seller@estateelite.lk', '$2b$10$txY5dL0VhJacDM3ZU3ZB/e.tylbysJIM9hI7noKnddf0ohjT2Q3jO', '0770000002', 'SELLER', NULL, NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 2);

INSERT INTO users (id, full_name, email, password, phone, role, agency_id, profile_image, created_at)
SELECT 3, 'Buyer Client', 'buyer@estateelite.lk', '$2b$10$txY5dL0VhJacDM3ZU3ZB/e.tylbysJIM9hI7noKnddf0ohjT2Q3jO', '0770000003', 'BUYER', NULL, NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 3);

INSERT INTO users (id, full_name, email, password, phone, role, agency_id, profile_image, created_at)
SELECT 4, 'Agent Smith', 'agent@estateelite.lk', '$2b$10$txY5dL0VhJacDM3ZU3ZB/e.tylbysJIM9hI7noKnddf0ohjT2Q3jO', '0770000004', 'AGENT', 1, NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 4);

INSERT INTO users (id, full_name, email, password, phone, role, agency_id, profile_image, created_at)
SELECT 5, 'Finance Officer', 'finance@estateelite.lk', '$2b$10$txY5dL0VhJacDM3ZU3ZB/e.tylbysJIM9hI7noKnddf0ohjT2Q3jO', '0770000005', 'FINANCE_OFFICER', NULL, NULL, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 5);

INSERT INTO properties (id, title, description, price, location, bedrooms, bathrooms, area_sqft, status, view_count, seller_id, created_at, updated_at)
SELECT 1, 'Modern Hillside Villa', 'A stunning 4-bedroom villa with panoramic hill views, open-plan living, and a private garden.', 42500000, 'Kandy', 4, 3, 3200, 'AVAILABLE', 0, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE id = 1);

INSERT INTO properties (id, title, description, price, location, bedrooms, bathrooms, area_sqft, status, view_count, seller_id, created_at, updated_at)
SELECT 2, 'Colombo City Apartment', 'Contemporary 2-bedroom apartment close to the city centre, ideal for young professionals.', 18500000, 'Colombo 05', 2, 2, 1100, 'AVAILABLE', 0, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE id = 2);

INSERT INTO properties (id, title, description, price, location, bedrooms, bathrooms, area_sqft, status, view_count, seller_id, created_at, updated_at)
SELECT 3, 'Beachfront Bungalow', 'Charming 3-bedroom bungalow steps away from the beach, perfect for a holiday home.', 31000000, 'Galle', 3, 2, 2000, 'AVAILABLE', 0, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM properties WHERE id = 3);

-- Agent branch districts (Sri Lanka)
UPDATE users SET branch = 'Colombo' WHERE email = 'agent@estateelite.lk';
