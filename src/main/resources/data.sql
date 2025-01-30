USE airline_db;

DELETE FROM bookings;
DELETE FROM flights;

INSERT INTO flights (flight_number, origin, destination, departure_time) VALUES
('AF123', 'Madrid', 'Paris', '2025-02-01 12:00:00'),
('LH456', 'Berlin', 'London', '2025-02-02 14:30:00'),
('BA789', 'Barcelona', 'New York', '2025-02-03 09:15:00');
