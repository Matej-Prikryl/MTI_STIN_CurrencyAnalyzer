package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.model.LiveRateResponse;
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
}