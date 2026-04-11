package cz.tul.stin.backend.model;

import java.util.Map;

public class LiveRateResponse extends RateResponse {
    private int timestamp;
    private Map<String, Double> quotes;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Double> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, Double> quotes) {
        this.quotes = quotes;
    }
}
