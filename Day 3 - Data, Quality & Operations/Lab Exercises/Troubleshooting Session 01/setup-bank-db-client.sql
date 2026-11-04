-- Bank database for the AccountStatementService data service (client version).
-- Replace <choose-a-password> and use the same password in AccountStatementService.dbs.
-- Run as a MySQL user that can create databases and users:  mysql -u root -p < setup-bank-db-client.sql

CREATE DATABASE IF NOT EXISTS bankdb;
CREATE USER IF NOT EXISTS 'bankuser'@'localhost' IDENTIFIED BY '<choose-a-password>';
GRANT ALL PRIVILEGES ON bankdb.* TO 'bankuser'@'localhost';
FLUSH PRIVILEGES;

USE bankdb;
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS Accounts;

CREATE TABLE Accounts (
  AccountNo    VARCHAR(10)   NOT NULL PRIMARY KEY,
  CustomerName VARCHAR(100)  NOT NULL,
  Branch       VARCHAR(50)   NOT NULL,
  AccountType  VARCHAR(20)   NOT NULL,
  Balance      DECIMAL(12,2) NOT NULL
);

CREATE TABLE Transactions (
  TxnId       INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
  AccountNo   VARCHAR(10)   NOT NULL,
  TxnDate     DATE          NOT NULL,
  TxnType     VARCHAR(10)   NOT NULL,
  Amount      DECIMAL(12,2) NOT NULL,
  Description VARCHAR(100)  NOT NULL,
  FOREIGN KEY (AccountNo) REFERENCES Accounts (AccountNo)
);

INSERT INTO Accounts VALUES
  ('1001', 'Nimal Perera',     'Colombo', 'SAVINGS', 250000.00),
  ('1002', 'Ayesha Fernando',  'Kandy',   'CURRENT', 980500.50),
  ('1003', 'Kamal Silva',      'Galle',   'SAVINGS',  45200.75),
  ('1004', 'Dilani Jayasinghe','Colombo', 'SAVINGS', 120000.00),
  ('1005', 'Ruwan Bandara',    'Jaffna',  'CURRENT', 610300.00);

INSERT INTO Transactions (AccountNo, TxnDate, TxnType, Amount, Description) VALUES
  ('1001', '2026-10-01', 'CREDIT', 150000.00, 'Salary'),
  ('1001', '2026-10-03', 'DEBIT',   12500.00, 'Electricity bill'),
  ('1001', '2026-10-10', 'DEBIT',   35000.00, 'ATM withdrawal'),
  ('1001', '2026-10-15', 'CREDIT',  20000.00, 'Transfer from 1004'),
  ('1002', '2026-10-02', 'CREDIT', 500000.00, 'Customer payment'),
  ('1002', '2026-10-05', 'DEBIT',  120000.00, 'Supplier payment'),
  ('1002', '2026-10-20', 'DEBIT',   75000.00, 'Rent'),
  ('1003', '2026-10-04', 'CREDIT',  40000.00, 'Salary'),
  ('1003', '2026-10-12', 'DEBIT',    5500.25, 'Mobile bill'),
  ('1004', '2026-10-15', 'DEBIT',   20000.00, 'Transfer to 1001'),
  ('1004', '2026-10-18', 'CREDIT',  60000.00, 'Fixed deposit interest'),
  ('1005', '2026-10-06', 'CREDIT', 300000.00, 'Customer payment'),
  ('1005', '2026-10-22', 'DEBIT',   90000.00, 'Payroll');

-- Check: both counts must match
-- SELECT COUNT(*) FROM bankdb.Accounts;       -- 5
-- SELECT COUNT(*) FROM bankdb.Transactions;   -- 13

-- Remove everything afterwards:
-- DROP DATABASE bankdb;
-- DROP USER 'bankuser'@'localhost';
