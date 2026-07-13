-- Script inicial vazio. O Flyway registrará a execução deste script criando a tabela flyway_schema_history no banco de dados.
-- O banco será versionado a partir daqui.

CREATE TABLE flyway_test (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);
