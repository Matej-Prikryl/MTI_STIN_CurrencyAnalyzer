package cz.tul.stin.backend.model;

import java.util.Map;

public class TimeframeRateResponse extends RateResponse {
    private boolean timeframe;
    private String start_date;
    private String end_date;
    private Map<String, Map<String, Double>> quotes;

    public boolean isTimeframe() {
        return timeframe;
    }

    public void setTimeframe(boolean timeframe) {
        this.timeframe = timeframe;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Map<String, Map<String, Double>> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, Map<String, Double>> quotes) {
        this.quotes = quotes;
    }
}
