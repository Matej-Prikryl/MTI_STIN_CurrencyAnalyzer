package cz.tul.stin.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class TimeframeRateResponse {
    private boolean success;
    private String source;
    private boolean timeframe;
    private String start_date;
    private String end_date;
    private Map<String, Map<String, Double>> quotes;

    private Map<String, Double> latestQuotes;

}
