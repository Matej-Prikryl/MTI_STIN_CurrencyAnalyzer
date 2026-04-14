package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;

@Service
@Profile("dev")
public class MockRateClient implements RateClient {

    @Override
    public TimeframeRateResponse getTimeframeRates(String base) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/mock-data/timeframe.json");
            var response = mapper.readValue(is, TimeframeRateResponse.class);
            Map<String, Double> rates = response.getQuotes().get("2010-03-01");
            response.setLatestQuotes(rates);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to load mock data", e);
        }
    }
}
