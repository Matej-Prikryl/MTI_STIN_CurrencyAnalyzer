package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.LiveRateResponse;
import cz.tul.stin.backend.model.TimeframeRateResponse;

public interface RateClient {
    LiveRateResponse getLiveRates(String base);
    TimeframeRateResponse getTimeframeRates(String base);
}
