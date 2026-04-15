package cz.tul.stin.backend.storage;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import cz.tul.stin.backend.service.RateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRepositoryTest {

    @Mock
    private RateClient rateClient;

    private CurrencyRepository currencyRepository;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2011-01-01T10:00:00Z"), ZoneId.of("UTC"));
        currencyRepository = new CurrencyRepository(rateClient, clock);
    }

    @Test
    void testGetLatestRates_FetchFromAPI_Success() {
        String base = "EUR";
        String startDate = "2010-01-01";
        String endDate = "2011-01-01";
        TimeframeRateResponse response = new TimeframeRateResponse();
        Map<String, Map<String, Double>> quotes = new HashMap<>();
        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.1);
        quotes.put("2011-01-01", day1);
        response.setQuotes(quotes);

        when(rateClient.getTimeframeRates(base, startDate, endDate)).thenReturn(response);

        Map<String, Double> result = currencyRepository.getLatestRates(base);

        assertNotNull(result);
        assertEquals(1.1, result.get("USD"));
        verify(rateClient, times(1)).getTimeframeRates(base,  startDate, endDate);
    }

    @Test
    void testGetLatestRates_UseCache() {
        String base = "EUR";
        String startDate = "2010-01-01";
        String endDate = "2011-01-01";
        TimeframeRateResponse response = new TimeframeRateResponse();
        Map<String, Map<String, Double>> quotes = new HashMap<>();
        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.1);
        quotes.put("2010-01-01", day1);
        response.setQuotes(quotes);

        when(rateClient.getTimeframeRates(base, startDate, endDate)).thenReturn(response);

        // First call fetches from API
        currencyRepository.getLatestRates(base);
        // Second call should use cache
        Map<String, Double> result = currencyRepository.getLatestRates(base);

        assertNotNull(result);
        assertEquals(1.1, result.get("USD"));
        verify(rateClient, times(1)).getTimeframeRates(base, startDate, endDate);
    }

    @Test
    void testInvalidateCache() {
        String base = "EUR";
        String startDate = "2010-01-01";
        String endDate = "2011-01-01";
        TimeframeRateResponse response = new TimeframeRateResponse();
        Map<String, Map<String, Double>> quotes = new HashMap<>();
        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.1);
        quotes.put("2010-01-01", day1);
        response.setQuotes(quotes);

        when(rateClient.getTimeframeRates(base, startDate, endDate)).thenReturn(response);

        // 1. Initial fetch (populates cache)
        currencyRepository.getLatestRates(base);

        // 2. Invalidate cache
        currencyRepository.invalidateCache();

        // 3. Second fetch (should trigger new API call)
        currencyRepository.getLatestRates(base);

        verify(rateClient, times(2)).getTimeframeRates(base, startDate, endDate);
    }

    @Test
    void testGetTimeframeRates_Success() {
        String base = "EUR";
        String startDate = "2010-01-01";
        String endDate = "2011-01-01";
        TimeframeRateResponse response = new TimeframeRateResponse();
        Map<String, Map<String, Double>> quotes = new HashMap<>();
        Map<String, Double> day1 = new HashMap<>();
        day1.put("USD", 1.1);
        quotes.put("2010-01-01", day1);
        response.setQuotes(quotes);

        when(rateClient.getTimeframeRates(base, startDate, endDate)).thenReturn(response);

        TreeMap<String, Map<String, Double>> result = currencyRepository.getTimeframeRates(base, "2010-01-01", "2011-01-03");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1.1, result.get("2010-01-01").get("USD"));
    }

    @Test
    void testGetLatestRates_APIReturnsNull_ThrowsException() {
        String base = "EUR";
        String startDate = "2010-01-01";
        String endDate = "2011-01-01";
        when(rateClient.getTimeframeRates(base, startDate, endDate)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> currencyRepository.getLatestRates(base));
    }

    @Test
    void testGetLatestRates_APIReturnsEmptyQuotes_ThrowsException() {
        String base = "EUR";
        String startDate = "2010-01-01";
        String endDate = "2011-01-01";
        TimeframeRateResponse response = new TimeframeRateResponse();
        response.setQuotes(new HashMap<>());
        when(rateClient.getTimeframeRates(base, startDate, endDate)).thenReturn(response);

        assertThrows(RuntimeException.class, () -> currencyRepository.getLatestRates(base));
    }
}