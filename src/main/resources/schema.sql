DROP TABLE IF EXISTS loan;

CREATE TABLE loan (
    loan_id INT PRIMARY KEY AUTO_INCREMENT,
    amount INT,
    current_status VARCHAR(20),
    status_message VARCHAR(255),
    user_id INT
);
