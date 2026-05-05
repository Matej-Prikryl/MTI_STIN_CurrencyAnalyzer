package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;

public interface RateClient {
    TimeframeRateResponse getTimeframeRates(String base, String startDate, String endDate);
}
