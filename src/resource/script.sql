CREATE DATABASE banco_digital;
USE banco_digital;

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    cpf VARCHAR(11) UNIQUE,
    login VARCHAR(50) UNIQUE,
    senha VARCHAR(100)
);

CREATE TABLE contas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20),
    tipo VARCHAR(20),
    saldo DOUBLE,
    cliente_id INT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE transacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conta_id INT,
    tipo VARCHAR(20), -- deposito, saque, transferencia
    valor DOUBLE,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conta_id) REFERENCES contas(id)
);
