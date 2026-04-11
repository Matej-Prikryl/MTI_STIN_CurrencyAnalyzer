package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.LiveRateResponse;

public interface RateClient {
    LiveRateResponse getLiveRates(String base);
}
