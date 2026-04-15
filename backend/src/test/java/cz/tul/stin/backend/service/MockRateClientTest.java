package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockRateClientTest {

    @Test
    void testTimeframeRateResponse_parsesJsonCorrectly() {
        MockRateClient client = new MockRateClient();

        TimeframeRateResponse response = client.getTimeframeRates("EUR", "2010-03-01", "2010-03-02");

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
}