package cz.tul.stin.backend.model;

import java.util.Map;

public class LiveRateResponse extends RateResponse {
    private int timestamp;
    private Map<String, Double> rates;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
