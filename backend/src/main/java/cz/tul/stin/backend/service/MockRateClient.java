package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;

@Service
@Primary
@Slf4j
public class MockRateClient implements RateClient {

    @Override
    public TimeframeRateResponse getTimeframeRates(String base, String startDate, String endDate) {
        log.info("USING MOCK DATA: Fetching timeframe rates for base: {} ({} to {})", base, startDate, endDate);
        try (InputStream is = getClass().getResourceAsStream("/mock-data/timeframe.json")) {
            ObjectMapper mapper = new ObjectMapper();
            var response = mapper.readValue(is, TimeframeRateResponse.class);
            Map<String, Double> rates = response.getQuotes().get(response.getStart_date());
            response.setLatestQuotes(rates);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to load mock data", e);
        }
    }
}
