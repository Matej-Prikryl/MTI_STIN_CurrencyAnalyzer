package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockRateClientTest {
    @Test
    void testLiveRateResponse_parsesJsonCorrectly() {
        MockRateClient client = new MockRateClient();

        LiveRateResponse response = client.getLiveRates("EUR");

        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success should be true");
        assertEquals("EUR", response.getSource(), "Base currency should be EUR");

        assertNotNull(response.getQuotes(), "Rates map should not be null");
        assertTrue(response.getQuotes().containsKey("EURCZK"), "Rates map should contain this key: EURCZK");

        assertEquals(24.75, response.getQuotes().get("EURCZK"), "The rate for CZK should match the mock JSON");
    }

    @Test
    void testTimeframeRateResponse_parsesJsonCorrectly() {
        MockRateClient client = new MockRateClient();

        TimeframeRateResponse response = client.getTimeframeRates("EUR");

        assertNotNull(response, "Response should not be null");
        assertTrue(response.isSuccess(), "Success should be true");
        assertEquals("EUR", response.getSource(), "Base currency should be EUR");

        assertTrue(response.isTimeframe(), "Response should be timeframe");
        assertNotNull(response.getStart_date());
        assertNotNull(response.getEnd_date());
        assertNotNull(response.getQuotes(), "Rates map should not be null");
        assertTrue(response.getQuotes().containsKey("2010-03-01"), "Rates map should contain this key: 2010-03-01");

        assertEquals(24.75, response.getQuotes().get("2010-03-01").get("EURCZK"), "The rate for CZK should match the mock JSON");
    }

    @Test
    void testLoadMockData_throwsExceptionOnInvalidPath() {
        MockRateClient client = new MockRateClient();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.loadMockData("/non-existent-path.json", TimeframeRateResponse.class);
        });

        assertTrue(exception.getMessage().contains("Error: Unable to load mock data"));
    }
}