-- Database schema for Restaurant Management System
-- Using Derby SQL syntax

-- Create a table to simulate ENUM for order status
CREATE TABLE order_status_enum (
    status VARCHAR(20) PRIMARY KEY
);

-- Insert the allowed status values
INSERT INTO order_status_enum (status) VALUES 
    ('PENDING'),
    ('CONFIRMED'),
    ('PREPARING'),
    ('READY'),
    ('DELIVERED'),
    ('CANCELLED');

-- Create customers table
CREATE TABLE customers (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_customers_email UNIQUE (email),
    CONSTRAINT uk_customers_phone UNIQUE (phone_number)
);

-- Create menu_items table
CREATE TABLE menu_items (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description CLOB,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create orders table
CREATE TABLE orders (
    id VARCHAR(20) PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_status
        FOREIGN KEY (status)
        REFERENCES order_status_enum (status)
);

-- Create order_items table
CREATE TABLE order_items (
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    order_id VARCHAR(20) NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items (id)
        ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_customers_phone ON customers(phone_number);
CREATE INDEX idx_menu_items_category ON menu_items(category);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_menu_item ON order_items(menu_item_id);

-- Create triggers to automatically update the updated_at column
-- Derby uses a different syntax for triggers
CREATE TRIGGER update_customers_modtime
    BEFORE UPDATE ON customers
    REFERENCING OLD AS old NEW AS new
    FOR EACH ROW
    SET new.updated_at = CURRENT_TIMESTAMP;

CREATE TRIGGER update_menu_items_modtime
    BEFORE UPDATE ON menu_items
    REFERENCING OLD AS old NEW AS new
    FOR EACH ROW
    SET new.updated_at = CURRENT_TIMESTAMP;

CREATE TRIGGER update_orders_modtime
    BEFORE UPDATE ON orders
    REFERENCING OLD AS old NEW AS new
    FOR EACH ROW
    SET new.updated_at = CURRENT_TIMESTAMP;

-- Create a function to generate order numbers
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TRIGGER AS $$
DECLARE
    prefix TEXT := 'ORD';
    suffix TEXT;
BEGIN
    -- Generate a random 5-digit number
    suffix := LPAD(FLOOR(RANDOM() * 100000)::TEXT, 5, '0');
    NEW.id := prefix || suffix;
    
    -- Ensure the generated ID is unique
    WHILE EXISTS (SELECT 1 FROM orders WHERE id = NEW.id) LOOP
        suffix := LPAD(FLOOR(RANDOM() * 100000)::TEXT, 5, '0');
        NEW.id := prefix || suffix;
    END LOOP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create a trigger to generate order numbers before insert
CREATE TRIGGER generate_order_id
    BEFORE INSERT ON orders
    FOR EACH ROW
    EXECUTE FUNCTION generate_order_number();
