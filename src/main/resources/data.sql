DELETE FROM flights;
INSERT INTO airports (name, code, city, country) VALUES 
('John F. Kennedy International Airport', 'JFK', 'New York', 'USA'),
('Heathrow Airport', 'LHR', 'London', 'UK'),
('Charles de Gaulle Airport', 'CDG', 'Paris', 'France');

INSERT INTO flights (flight_number, origin_airport_id, destination_airport_id, departure_time, available_seats, available)
VALUES ('AF123', 1, 2, '2025-02-01 12:00:00', 1, true),
       ('LH456', 2, 3, '2025-01-01 14:30:00', 5, true),
       ('BA789', 3, 1, '2025-02-03 09:15:00', 2, true);

-- -- Очистка таблиц перед вставкой данных (если необходимо)
-- DELETE FROM bookings;
-- DELETE FROM flights;
-- DELETE FROM users_roles;
-- DELETE FROM users;
-- DELETE FROM roles;

-- -- Заполнение таблицы ролей
-- INSERT INTO roles (id, name) VALUES 
-- (1, 'ROLE_USER'), 
-- (2, 'ROLE_ADMIN');

-- -- Заполнение таблицы пользователей с хешированными паролями и аватарками
-- -- Пароли нужно захешировать заранее с BCrypt (Spring сам не хеширует data.sql)
-- INSERT INTO users (id, username, password, avatar) VALUES
-- (1, 'admin', '$2a$10$hG3U8VUyOsBy1Yg/YqX7fe8.Yv7qrrbItNcGzoxBc5KDrV8FQEl0C', 'https://example.com/avatars/admin.jpg'), -- пароль: admin123
-- (2, 'user', '$2a$10$uGhElDdV1M7EXrbcgF8nY.vz8tIzX7t.qKvYwFy7gByH9wB9Jq9C2', 'https://example.com/avatars/user.jpg');  -- пароль: user123

-- -- Заполнение таблицы user_roles (связь пользователей и ролей)
-- INSERT INTO users_roles (user_id, role_id) VALUES
-- (1, 2), -- Админ
-- (2, 1); -- Обычный пользователь

-- -- Заполнение таблицы рейсов
-- INSERT INTO flights (id, origin, destination, departure_time, seats_available, available) VALUES
-- (1, 'Madrid', 'Barcelona', '2025-02-01 10:00:00', 1, false),
-- (2, 'Alicante', 'London', '2025-02-02 15:30:00', 5, true),
-- (3, 'Paris', 'Berlin', '2025-02-03 12:00:00', 2, true);

-- -- Заполнение таблицы бронирований с новым реквизитом (например, номер билета или класс бронирования)
-- INSERT INTO bookings (id, user_id, flight_id, booking_time, status, ticket_number) VALUES
-- (1, 2, 1, '2025-01-29 08:00:00', 'CONFIRMED', 'ABC123'),
-- (2, 2, 2, '2025-01-29 09:00:00', 'PENDING', 'DEF456'),
-- (3, 2, 3, '2025-01-29 10:00:00', 'CANCELLED', 'GHI789');
