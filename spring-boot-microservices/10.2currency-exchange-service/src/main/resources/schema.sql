CREATE TABLE exchange_value (
    id BIGINT PRIMARY KEY,
    currency_from VARCHAR(255),
    currency_to VARCHAR(255),
    conversion_multiple DECIMAL(10,2),
    port INT
);
