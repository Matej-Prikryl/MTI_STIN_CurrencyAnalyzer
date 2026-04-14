package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyExtremes;
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
    private RateClient mockClient;

    @InjectMocks
    private RateCalculatorService calculator;

    @Test
    void testGetExtremes_returnsCorrectExtremes() {
        LiveRateResponse mockResponse = new LiveRateResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("CZK", 25.0);
        rates.put("USD", 1.1);
        rates.put("HUF", 390.0);
        mockResponse.setQuotes(rates);

        when(mockClient.getLiveRates("EUR")).thenReturn(mockResponse);

        CurrencyExtremes result = calculator.getExtremes("EUR");

        assertNotNull(result);
        assertEquals("USD", result.strongest().getKey(), "The strongest currency should be USD");
        assertEquals(1.1, result.strongest().getValue());

        assertEquals("HUF", result.weakest().getKey(), "The weakest currency should be HUF");
        assertEquals(390.0, result.weakest().getValue());
    }

    @Test
    void testGetExtremes_throwsExceptionIfDataIsMissing() {
        LiveRateResponse mockResponse = new LiveRateResponse();
        mockResponse.setQuotes(new HashMap<>());

        when(mockClient.getLiveRates("EUR")).thenReturn(mockResponse);

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            calculator.getExtremes("EUR");
        });

        assertEquals("Error: No rates to compare.", e.getMessage());
    }

    @Test
    void testGetAverageRates_returnsCorrectAverages() {
        TimeframeRateResponse mockResponse = new TimeframeRateResponse();
        Map<String, Map<String, Double>> quotes = new HashMap<>();

        Map<String, Double> dayOneRates = new HashMap<>();
        dayOneRates.put("EURCZK", 24.75);
        dayOneRates.put("EURUSD", 1.08);

        Map<String, Double> dayTwoRates = new HashMap<>();
        dayTwoRates.put("EURCZK", 24.25);
        dayTwoRates.put("EURUSD", 1.12);
        dayTwoRates.put("EURGBP", 0.86);

        quotes.put("2025-01-01", dayOneRates);
        quotes.put("2025-01-02", dayTwoRates);
        mockResponse.setQuotes(quotes);

        when(mockClient.getTimeframeRates("EUR")).thenReturn(mockResponse);

        Map<String, Double> result = calculator.getAverageRates("EUR");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(24.5, result.get("EURCZK"));
        assertEquals(1.1, result.get("EURUSD"));
        assertEquals(0.86, result.get("EURGBP"));
    }

    @Test
    void testGetAverageRates_ignoresMissingDailyRates() {
        TimeframeRateResponse mockResponse = new TimeframeRateResponse();
        Map<String, Map<String, Double>> quotes = new HashMap<>();

        Map<String, Double> dayOneRates = new HashMap<>();
        dayOneRates.put("EURCZK", 24.75);
        dayOneRates.put("EURUSD", 1.08);

        quotes.put("2025-01-01", dayOneRates);
        quotes.put("2025-01-02", null);
        mockResponse.setQuotes(quotes);

        when(mockClient.getTimeframeRates("EUR")).thenReturn(mockResponse);

        Map<String, Double> result = calculator.getAverageRates("EUR");

        assertEquals(24.75, result.get("EURCZK"));
        assertEquals(1.08, result.get("EURUSD"));
    }

    @Test
    void testGetAverageRates_throwsExceptionIfDataIsMissing() {
        TimeframeRateResponse mockResponse = new TimeframeRateResponse();
        mockResponse.setQuotes(new HashMap<>());

        when(mockClient.getTimeframeRates("EUR")).thenReturn(mockResponse);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.getAverageRates("EUR");
        });

        assertEquals("No data available for the given timeframe.", exception.getMessage());
    }

    @Test
    void testGetAverageRates_throwsExceptionIfQuotesAreNull() {
        TimeframeRateResponse mockResponse = new TimeframeRateResponse();
        mockResponse.setQuotes(null);

        when(mockClient.getTimeframeRates("EUR")).thenReturn(mockResponse);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.getAverageRates("EUR");
        });

        assertEquals("No data available for the given timeframe.", exception.getMessage());
    }
}
