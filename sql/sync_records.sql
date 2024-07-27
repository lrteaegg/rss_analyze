CREATE TABLE sync_records (
      id INT AUTO_INCREMENT PRIMARY KEY,
      sync_time DATETIME NOT NULL,
      sync_status ENUM('success', 'failure') NOT NULL,
      sync_type VARCHAR(50) NOT NULL,
      error_message TEXT
);
