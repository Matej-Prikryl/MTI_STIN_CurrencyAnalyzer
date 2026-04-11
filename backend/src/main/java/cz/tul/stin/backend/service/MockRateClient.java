package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.LiveRateResponse;
import cz.tul.stin.backend.model.RateResponse;
import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@Service
@Profile("dev")
public class MockRateClient implements RateClient {

    @Override
    public LiveRateResponse getLiveRates(String base) {
        return loadMockData("/mock-data/live.json", LiveRateResponse.class);
    }

    @Override
    public TimeframeRateResponse getTimeframeRates(String base) {
        return loadMockData("/mock-data/timeframe.json", TimeframeRateResponse.class);
    }

    protected <T extends RateResponse> T loadMockData(String path, Class<T> responseType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream(path);
            return mapper.readValue(is, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to load mock data", e);
        }
    }
}
