CREATE TABLE events (
    event_id VARCHAR(36) PRIMARY KEY NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(36) NOT NULL,
    aggregate_version INT NOT NULL,
    occurred_on DATETIME(6) NOT NULL,
    payload JSON NOT NULL
);

CREATE INDEX idx_events_aggregate_id_and_aggregate_version ON events (aggregate_id, aggregate_version);