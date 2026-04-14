package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCalculatorServiceTest {

    @Mock
    private RateClient rateClient;

    @InjectMocks
    private RateCalculatorService rateCalculatorService;

    @Test
    void testGetCurrencyInfo_Success() {
        // Prepare mock data
        String base = "EUR";
        TimeframeRateResponse response = new TimeframeRateResponse();
        
        // Mocking latest quotes for getExtremes
        Map<String, Double> latestQuotes = new HashMap<>();
        latestQuotes.put("USD", 1.05);
        latestQuotes.put("CZK", 24.5);
        latestQuotes.put("GBP", 0.85);
        response.setLatestQuotes(latestQuotes);

        // Mocking quotes for getAverageRates
        Map<String, Map<String, Double>> quotes = new HashMap<>();
        
        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.0);
        day1.put("CZK", 24.0);
        quotes.put("2026-01-01", day1);

        Map<String, Double> day2 = new HashMap<>();
        day2.put("USD", 1.1);
        day2.put("CZK", 25.0);
        quotes.put("2026-01-02", day2);

        response.setQuotes(quotes);

        when(rateClient.getTimeframeRates(base)).thenReturn(response);

        // Execute service
        var result = rateCalculatorService.getCurrencyInfo(base);

        // Assertions for averages
        assertNotNull(result);
        assertEquals(2, result.averages().size());
        assertEquals(1.05, result.averages().get("USD"));
        assertEquals(24.5, result.averages().get("CZK"));

        // Assertions for extremes
        assertEquals("GBP", result.extremes().strongest().getKey());
        assertEquals(0.85, result.extremes().strongest().getValue());
        assertEquals("CZK", result.extremes().weakest().getKey());
        assertEquals(24.5, result.extremes().weakest().getValue());
    }

    @Test
    void testGetCurrencyInfo_EmptyQuotesThrowsException() {
        String base = "EUR";
        TimeframeRateResponse response = new TimeframeRateResponse();
        response.setLatestQuotes(Map.of("USD", 1.0));
        response.setQuotes(new HashMap<>());

        when(rateClient.getTimeframeRates(base)).thenReturn(response);

        assertThrows(IllegalArgumentException.class, () -> rateCalculatorService.getCurrencyInfo(base));
    }

    @Test
    void testGetCurrencyInfo_EmptyLatestQuotesThrowsException() {
        String base = "EUR";
        TimeframeRateResponse response = new TimeframeRateResponse();
        response.setLatestQuotes(new HashMap<>());
        response.setQuotes(Map.of("2026-01-01", Map.of("USD", 1.0)));

        when(rateClient.getTimeframeRates(base)).thenReturn(response);

        assertThrows(RuntimeException.class, () -> rateCalculatorService.getCurrencyInfo(base));
    }

    @Test
    void testGetCurrencyInfo_NullLatestQuotesThrowsException() {
        String base = "EUR";
        TimeframeRateResponse response = new TimeframeRateResponse();
        response.setLatestQuotes(null);
        response.setQuotes(Map.of("2026-01-01", Map.of("USD", 1.0)));

        when(rateClient.getTimeframeRates(base)).thenReturn(response);

        assertThrows(RuntimeException.class, () -> rateCalculatorService.getCurrencyInfo(base));
    }
}
