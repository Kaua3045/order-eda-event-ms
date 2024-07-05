CREATE TABLE outbox (
    event_id VARCHAR(36) PRIMARY KEY NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(36) NOT NULL,
    aggregate_version INT NOT NULL,
    occurred_on DATETIME(6) NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE INDEX idx_outbox_occurred_on_and_status ON outbox (occurred_on, status);