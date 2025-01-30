DELETE FROM flights;
INSERT INTO airports (name, code, city, country) VALUES 
('John F. Kennedy International Airport', 'JFK', 'New York', 'USA'),
('Heathrow Airport', 'LHR', 'London', 'UK'),
('Charles de Gaulle Airport', 'CDG', 'Paris', 'France');

INSERT INTO flights (flight_number, origin_airport_id, destination_airport_id, departure_time) VALUES
('AF123', 1, 2, '2025-02-01 12:00:00'),
('LH456', 2, 3, '2025-02-02 14:30:00'),
('BA789', 3, 1, '2025-02-03 09:15:00');
