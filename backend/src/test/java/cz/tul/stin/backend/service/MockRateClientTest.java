package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.LiveRateResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class MockRateClientTest {
    @Test
    void shouldParseJsonCorrectly() {
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
    void shouldThrowExceptionWhenDataIsMissing() {
        MockRateClient clientSpy = spy(new MockRateClient());
        doReturn(null).when(clientSpy).getMockDataStream();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientSpy.getLiveRates("EUR");
        });
        assertEquals("Error: Unable to load mock data", exception.getMessage());
    }
}