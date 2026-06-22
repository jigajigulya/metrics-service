--changeset matdev:1
CREATE TABLE metrics (
                         id BIGSERIAL PRIMARY KEY,
                         client_id VARCHAR(255) NOT NULL,
                         timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
                         value NUMERIC(19, 4) NOT NULL,
                         payload JSONB NOT NULL
);


CREATE INDEX idx_metrics_timestamp ON metrics (timestamp);
