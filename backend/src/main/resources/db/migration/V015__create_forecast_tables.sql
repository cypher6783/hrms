-- V015: Forecast tables

CREATE TABLE forecast_snapshots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forecast_type VARCHAR(30) NOT NULL,
    forecast_horizon VARCHAR(10) NOT NULL,
    target_period_start DATE NOT NULL,
    target_period_end DATE NOT NULL,
    predicted_values JSONB NOT NULL,
    model_used VARCHAR(30) NOT NULL,
    accuracy_score DECIMAL(5, 2),
    generated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_forecast_snapshots_type ON forecast_snapshots(forecast_type);
CREATE INDEX idx_forecast_snapshots_period ON forecast_snapshots(target_period_start, target_period_end);
