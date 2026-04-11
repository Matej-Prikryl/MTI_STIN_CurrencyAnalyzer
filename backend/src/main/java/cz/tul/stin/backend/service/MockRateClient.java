package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.LiveRateResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class MockRateClient implements RateClient {
    @Override
    public LiveRateResponse getLiveRates(String base) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("mock-data/live.json");
            return mapper.readValue(is, LiveRateResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to load mock data", e);
        }
    }
}
