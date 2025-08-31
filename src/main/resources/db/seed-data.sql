-- FirstClub Membership Program - Seed Data
-- This script populates the database with initial membership plans and tiers

-- Clear existing data (if any)
DELETE FROM subscription_history;
DELETE FROM subscriptions;
DELETE FROM tier_upgrade_rules;
DELETE FROM membership_plans;
DELETE FROM membership_tiers;
DELETE FROM orders;
DELETE FROM users;

-- Reset auto-increment counters
ALTER SEQUENCE membership_tiers_id_seq RESTART WITH 1;
ALTER SEQUENCE membership_plans_id_seq RESTART WITH 1;
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE subscriptions_id_seq RESTART WITH 1;
ALTER SEQUENCE orders_id_seq RESTART WITH 1;
ALTER SEQUENCE subscription_history_id_seq RESTART WITH 1;

-- Insert Membership Tiers
INSERT INTO membership_tiers (name, description, tier_level, min_orders_required, min_monthly_order_value, 
                             cohort_restriction, benefits_description, discount_percentage, is_active, 
                             created_at, updated_at) VALUES
-- Silver Tier - Basic membership
('Silver', 'Basic membership tier with essential benefits', 1, 0, 0.00, NULL, 
 'Free shipping on orders over $50, 5% discount on selected items, Priority customer support', 5.00, true, 
 NOW(), NOW()),

-- Gold Tier - Mid-level membership  
('Gold', 'Premium membership tier with enhanced benefits', 2, 5, 200.00, NULL,
 'Free shipping on all orders, 10% discount on all items, VIP customer support, Early access to sales, Birthday rewards', 10.00, true,
 NOW(), NOW()),

-- Platinum Tier - Elite membership
('Platinum', 'Elite membership tier with exclusive benefits', 3, 10, 500.00, NULL,
 'Free shipping on all orders, 15% discount on all items, 24/7 VIP customer support, Exclusive product access, Personal shopping assistant, Invitation to exclusive events, Quarterly gift box', 15.00, true,
 NOW(), NOW());

-- Insert Membership Plans
INSERT INTO membership_plans (name, description, duration_months, price, discount_percentage, 
                             max_tier_level, is_active, created_at, updated_at) VALUES
-- Monthly Plans
('Monthly Silver', 'Monthly subscription to Silver tier membership', 1, 19.99, 0.00, 1, true, NOW(), NOW()),
('Monthly Gold', 'Monthly subscription to Gold tier membership', 1, 39.99, 5.00, 2, true, NOW(), NOW()),
('Monthly Platinum', 'Monthly subscription to Platinum tier membership', 1, 79.99, 10.00, 3, true, NOW(), NOW()),

-- Quarterly Plans (with quarterly discount)
('Quarterly Silver', 'Quarterly subscription to Silver tier membership with savings', 3, 59.97, 10.00, 1, true, NOW(), NOW()),
('Quarterly Gold', 'Quarterly subscription to Gold tier membership with savings', 3, 119.97, 15.00, 2, true, NOW(), NOW()),
('Quarterly Platinum', 'Quarterly subscription to Platinum tier membership with savings', 3, 239.97, 20.00, 3, true, NOW(), NOW()),

-- Yearly Plans (with annual discount)
('Yearly Silver', 'Annual subscription to Silver tier membership with maximum savings', 12, 239.88, 20.00, 1, true, NOW(), NOW()),
('Yearly Gold', 'Annual subscription to Gold tier membership with maximum savings', 12, 479.88, 25.00, 2, true, NOW(), NOW()),
('Yearly Platinum', 'Annual subscription to Platinum tier membership with maximum savings', 12, 959.88, 30.00, 3, true, NOW(), NOW());


-- Insert Tier Upgrade Rules
INSERT INTO tier_upgrade_rules (source_tier_id, target_tier_id, rule_name, rule_description, upgrade_type, 
                               min_orders_required, min_monthly_order_value, min_membership_duration_days, 
                               cohort_restriction, evaluation_frequency_days, auto_upgrade, requires_approval, 
                               is_active, created_at, updated_at) VALUES
-- Silver to Gold upgrade rule
(1, 2, 'Silver to Gold Auto-Upgrade', 'Automatic upgrade from Silver to Gold tier based on order activity', 'AUTOMATIC', 5, 200.00, 30, NULL, 30, true, false, true, NOW(), NOW()),

-- Gold to Platinum upgrade rule  
(2, 3, 'Gold to Platinum Auto-Upgrade', 'Automatic upgrade from Gold to Platinum tier based on order activity', 'AUTOMATIC', 10, 500.00, 60, NULL, 30, true, false, true, NOW(), NOW()),

-- Silver to Platinum direct upgrade rule
(1, 3, 'Silver to Platinum Direct Upgrade', 'Direct upgrade from Silver to Platinum for high-value customers', 'PERFORMANCE_BASED', 15, 1000.00, 90, 'VIP', 30, false, true, true, NOW(), NOW());
