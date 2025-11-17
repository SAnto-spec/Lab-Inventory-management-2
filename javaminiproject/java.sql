
-- Lab Inventory Management System Database Schema

-- Equipment Table
CREATE TABLE IF NOT EXISTS equipments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    lower_limit INTEGER NOT NULL,
    unit_price REAL NOT NULL,
    expiry_date DATE,
    location TEXT,
    supplier TEXT,
    date_added DATE DEFAULT CURRENT_DATE
);

-- Orders Table (for tracking equipment orders)
CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    equipment_id INTEGER NOT NULL,
    equipment_name TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    order_date DATE DEFAULT CURRENT_DATE,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    status TEXT NOT NULL CHECK(status IN ('PENDING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED')),
    supplier TEXT NOT NULL,
    total_cost REAL,
    FOREIGN KEY (equipment_id) REFERENCES equipments(id)
);

-- Sample Equipment Data (Prices converted from USD to INR at rate 1 USD = 83 INR)
INSERT INTO equipments (name, category, quantity, lower_limit, unit_price, expiry_date, location, supplier) VALUES
('Microscope', 'Optical Equipment', 15, 5, 37350.00, NULL, 'Lab A - Shelf 1', 'ScienceSupplyCo'),
('Test Tubes (Box of 100)', 'Glassware', 8, 10, 2075.00, NULL, 'Storage Room B', 'LabGlass Inc'),
('Pipettes (10ml)', 'Lab Tools', 25, 15, 1037.50, NULL, 'Lab C - Drawer 2', 'PrecisionLab'),
('Petri Dishes (Pack of 20)', 'Glassware', 12, 10, 1494.00, NULL, 'Storage Room B', 'LabGlass Inc'),
('Safety Goggles', 'Safety Equipment', 30, 20, 705.50, NULL, 'Safety Cabinet', 'SafetyFirst Ltd'),
('Lab Coats', 'Safety Equipment', 18, 15, 2905.00, NULL, 'Storage Room A', 'MedicalSupply Co'),
('Beakers (500ml)', 'Glassware', 20, 12, 1245.00, NULL, 'Lab A - Shelf 3', 'LabGlass Inc'),
('Bunsen Burner', 'Heating Equipment', 10, 5, 7055.00, NULL, 'Lab B - Counter', 'ScienceSupplyCo'),
('pH Meter', 'Measurement Tools', 6, 3, 9960.00, NULL, 'Lab C - Cabinet', 'PrecisionLab'),
('Thermometer (Digital)', 'Measurement Tools', 14, 8, 1826.00, NULL, 'Lab A - Drawer 1', 'PrecisionLab'),
('Ethanol (1L)', 'Chemicals', 5, 8, 2490.00, '2025-12-15', 'Chemical Storage', 'ChemSupply Ltd'),
('Distilled Water (5L)', 'Chemicals', 20, 15, 664.00, '2026-01-20', 'Storage Room B', 'ChemSupply Ltd'),
('Sodium Chloride (500g)', 'Chemicals', 10, 6, 996.00, '2026-06-30', 'Chemical Storage', 'ChemSupply Ltd'),
('Hydrochloric Acid (1L)', 'Chemicals', 4, 5, 3735.00, '2025-11-25', 'Chemical Storage', 'ChemSupply Ltd'),
('Latex Gloves (Box of 100)', 'Safety Equipment', 7, 12, 1494.00, '2025-12-01', 'Safety Cabinet', 'SafetyFirst Ltd'),
('Autoclave', 'Sterilization', 3, 2, 207500.00, NULL, 'Sterilization Room', 'MedicalSupply Co'),
('Centrifuge', 'Lab Equipment', 4, 2, 149400.00, NULL, 'Lab C - Counter', 'ScienceSupplyCo'),
('Weighing Scale (Digital)', 'Measurement Tools', 8, 4, 20750.00, NULL, 'Lab B - Counter', 'PrecisionLab'),
('Forceps', 'Lab Tools', 22, 15, 788.50, NULL, 'Lab A - Drawer 3', 'ScienceSupplyCo'),
('Scalpels (Pack of 10)', 'Lab Tools', 6, 10, 2324.00, NULL, 'Lab B - Cabinet', 'MedicalSupply Co'),
('Filter Paper (Pack of 100)', 'Consumables', 15, 10, 1826.00, NULL, 'Storage Room B', 'LabGlass Inc'),
('Agar Powder (500g)', 'Growth Media', 3, 5, 4565.00, '2025-12-10', 'Chemical Storage', 'BioSupply Co'),
('Culture Flasks', 'Glassware', 18, 10, 2656.00, NULL, 'Lab C - Shelf 2', 'LabGlass Inc'),
('Syringes (10ml - Pack of 50)', 'Medical Supplies', 9, 12, 2905.00, '2025-12-05', 'Medical Cabinet', 'MedicalSupply Co'),
('Cotton Swabs (Pack of 200)', 'Consumables', 11, 10, 705.50, NULL, 'Storage Room A', 'MedicalSupply Co'),
('Incubator', 'Lab Equipment', 2, 1, 265600.00, NULL, 'Lab C - Room 2', 'BioSupply Co'),
('Spectrophotometer', 'Optical Equipment', 3, 2, 373500.00, NULL, 'Lab A - Counter', 'ScienceSupplyCo'),
('Refrigerator (Lab Grade)', 'Storage Equipment', 4, 2, 124500.00, NULL, 'Storage Rooms', 'CoolStore Inc'),
('Sanitizer Dispenser', 'Safety Equipment', 12, 8, 3735.00, NULL, 'All Labs', 'SafetyFirst Ltd'),
('Wheelchair', 'Medical Equipment', 2, 1, 70550.00, NULL, 'Emergency Room', 'MedicalSupply Co');

-- Sample Orders Data (Prices converted from USD to INR at rate 1 USD = 83 INR)
INSERT INTO orders (equipment_id, equipment_name, quantity, order_date, expected_delivery_date, status, supplier, total_cost) VALUES
(2, 'Test Tubes (Box of 100)', 15, '2025-11-10', '2025-11-20', 'IN_TRANSIT', 'LabGlass Inc', 31125.00),
(11, 'Ethanol (1L)', 10, '2025-11-12', '2025-11-22', 'IN_TRANSIT', 'ChemSupply Ltd', 24900.00),
(15, 'Latex Gloves (Box of 100)', 20, '2025-11-14', '2025-11-24', 'PENDING', 'SafetyFirst Ltd', 29880.00),
(20, 'Scalpels (Pack of 10)', 15, '2025-11-08', '2025-11-18', 'IN_TRANSIT', 'MedicalSupply Co', 34860.00),
(22, 'Agar Powder (500g)', 8, '2025-11-15', '2025-11-25', 'PENDING', 'BioSupply Co', 36520.00),
(24, 'Syringes (10ml - Pack of 50)', 10, '2025-11-05', '2025-11-15', 'DELIVERED', 'MedicalSupply Co', 29050.00),
(14, 'Hydrochloric Acid (1L)', 6, '2025-11-13', '2025-11-23', 'PENDING', 'ChemSupply Ltd', 22410.00);

