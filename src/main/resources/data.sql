INSERT INTO users (login, name, password, role) VALUES
        ('userlogin', 'username', '$2a$10$rCxMoD7AFUJQKOkk7.E/zOtHRBicUO3XO.sqdIN5JrRvnss3bapUK', 'USER'),
        ('adminlogin', 'adminame', '$2a$10$rCxMoD7AFUJQKOkk7.E/zOtHRBicUO3XO.sqdIN5JrRvnss3bapUK', 'ADMIN'),
        ('managerlogin', 'managername', '$2a$10$rCxMoD7AFUJQKOkk7.E/zOtHRBicUO3XO.sqdIN5JrRvnss3bapUK', 'MANAGER')
    ON CONFLICT (login) DO NOTHING;