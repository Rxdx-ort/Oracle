CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user VARCHAR(50),
    password VARCHAR(50),
    email VARCHAR(100)
);


CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    age INT,
    address VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    time TIME,
    description VARCHAR(200)
);

CREATE TABLE patients_appointments (
    patient_id INT,
    appointment_id INT,
    PRIMARY KEY (patient_id, appointment_id),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

ALTER TABLE appointments
ADD CONSTRAINT unique_datetime UNIQUE (date, time);


INSERT INTO appointments (date, time, description) VALUES ('2024-04-28', '09:00:00', 'Revision de brackets');
INSERT INTO patients (first_name, last_name, age, address, phone, email) 
VALUES ('Mike', 'Larrea', 22, 'Senderos', '6543-4321', 'mike@example.com');


SELECT p.*, a.*
FROM patients p
JOIN patients_appointments pa ON p.id = pa.patient_id
JOIN appointments a ON pa.appointment_id = a.id;



