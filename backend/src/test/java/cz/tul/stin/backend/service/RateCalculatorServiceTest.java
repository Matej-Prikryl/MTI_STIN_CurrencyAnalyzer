package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.storage.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCalculatorServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private RateCalculatorService rateCalculatorService;

    @Test
    void testGetCurrencyInfo_Success() {
        // Prepare mock data
        String base = "EUR";
        String startDate = "2026-01-01";
        String endDate = "2026-01-02";
        Set<String> targetCurrencies = Set.of("USD", "CZK", "GBP");

        // Mocking latest rates for getExtremes
        Map<String, Double> latestRates = new HashMap<>();
        latestRates.put("USD", 1.05);
        latestRates.put("CZK", 24.5);
        latestRates.put("GBP", 0.85);
        latestRates.put("JPY", 150.0); // Should be filtered out

        // Mocking timeframe rates for getAverageRates
        TreeMap<String, Map<String, Double>> rates = new TreeMap<>();
        
        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.0);
        day1.put("CZK", 24.0);
        day1.put("JPY", 140.0);
        rates.put("2026-01-01", day1);

        Map<String, Double> day2 = new HashMap<>();
        day2.put("USD", 1.1);
        day2.put("CZK", 25.0);
        day2.put("JPY", 145.0);
        rates.put("2026-01-02", day2);

        when(currencyRepository.getLatestRates(base)).thenReturn(latestRates);
        when(currencyRepository.getTimeframeRates(base, startDate, endDate)).thenReturn(rates);

        // Execute service
        CurrencyInfo result = rateCalculatorService.getCurrencyInfo(base, startDate, endDate, targetCurrencies);

        // Assertions for averages
        assertNotNull(result);
        assertEquals(2, result.averages().size());
        assertFalse(result.averages().containsKey("JPY"));
        assertEquals(1.05, result.averages().get("USD"));
        assertEquals(24.5, result.averages().get("CZK"));

        // Assertions for extremes
        assertEquals("GBP", result.extremes().strongest().key());
        assertEquals(0.85, result.extremes().strongest().value());
        assertEquals("CZK", result.extremes().weakest().key());
        assertEquals(24.5, result.extremes().weakest().value());
    }

    @Test
    void testGetCurrencyInfo_EmptyTimeframeRatesThrowsException() {
        String base = "EUR";
        String startDate = "2026-01-01";
        String endDate = "2026-01-02";
        Set<String> targetCurrencies = Set.of("USD");

        when(currencyRepository.getLatestRates(base)).thenReturn(Map.of("USD", 1.0));
        when(currencyRepository.getTimeframeRates(base, startDate, endDate)).thenReturn(new TreeMap<>());

        assertThrows(IllegalArgumentException.class, () -> rateCalculatorService.getCurrencyInfo(base, startDate, endDate, targetCurrencies));
    }

    @Test
    void testGetCurrencyInfo_EmptyLatestRatesThrowsException() {
        String base = "EUR";
        String startDate = "2026-01-01";
        String endDate = "2026-01-02";
        Set<String> targetCurrencies = Set.of("USD");

        when(currencyRepository.getLatestRates(base)).thenReturn(new HashMap<>());

        assertThrows(RuntimeException.class, () -> rateCalculatorService.getCurrencyInfo(base, startDate, endDate, targetCurrencies));
    }

    @Test
    void testGetCurrencyInfo_NullLatestRatesThrowsException() {
        String base = "EUR";
        String startDate = "2026-01-01";
        String endDate = "2026-01-02";
        Set<String> targetCurrencies = Set.of("USD");

        when(currencyRepository.getLatestRates(base)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> rateCalculatorService.getCurrencyInfo(base, startDate, endDate, targetCurrencies));
    }
}
