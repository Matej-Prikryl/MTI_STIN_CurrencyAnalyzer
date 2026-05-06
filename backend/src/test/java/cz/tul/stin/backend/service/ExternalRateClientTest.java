package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExternalRateClientTest {

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private ExternalRateClient setupClient(RestTemplate mockRt) throws Exception {
        ExternalRateClient client = new ExternalRateClient();
        setPrivateField(client, "apiUrl", "http://api.test");
        setPrivateField(client, "apiKey", "MYKEY");
        Field rtField = ExternalRateClient.class.getDeclaredField("restTemplate");
        rtField.setAccessible(true);
        rtField.set(client, mockRt);
        return client;
    }

    @Test
    void getTimeframeRates_success_callsRestTemplateWithExpectedUrl() throws Exception {
        RestTemplate mockRt = Mockito.mock(RestTemplate.class);
        TimeframeRateResponse resp = new TimeframeRateResponse();
        resp.setSuccess(true);
        Map<String, Map<String, Double>> quotes = new HashMap<>();
        quotes.put("2020-01-01", new HashMap<>());
        resp.setQuotes(quotes);
        Mockito.when(mockRt.getForObject(Mockito.anyString(), Mockito.eq(TimeframeRateResponse.class))).thenReturn(resp);

        ExternalRateClient client = setupClient(mockRt);

        // call
        TimeframeRateResponse result = client.getTimeframeRates("EUR", "2020-01-01", "2020-01-10");
        
        // verify
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockRt).getForObject(captor.capture(), Mockito.eq(TimeframeRateResponse.class));
        String url = captor.getValue();
        assertTrue(url.startsWith("http://api.test"));
        assertTrue(url.contains("access_key=MYKEY"));
        assertTrue(url.contains("source=EUR"));
        assertTrue(url.contains("start_date=2020-01-01"));
        assertTrue(url.contains("end_date=2020-01-10"));
        assertTrue(url.contains("currencies="));
        assertSame(resp, result);
    }

    @Test
    void getTimeframeRates_nullResponse_returnsNull() throws Exception {
        RestTemplate mockRt = Mockito.mock(RestTemplate.class);
        Mockito.when(mockRt.getForObject(Mockito.anyString(), Mockito.eq(TimeframeRateResponse.class))).thenReturn(null);

        ExternalRateClient client = setupClient(mockRt);

        TimeframeRateResponse result = client.getTimeframeRates("EUR", "2020-01-01", "2020-01-10");
        assertNull(result);
    }

    @Test
    void getTimeframeRates_unsuccessfulResponse_returnsResponse() throws Exception {
        RestTemplate mockRt = Mockito.mock(RestTemplate.class);
        TimeframeRateResponse resp = new TimeframeRateResponse();
        resp.setSuccess(false);
        Mockito.when(mockRt.getForObject(Mockito.anyString(), Mockito.eq(TimeframeRateResponse.class))).thenReturn(resp);

        ExternalRateClient client = setupClient(mockRt);

        TimeframeRateResponse result = client.getTimeframeRates("EUR", "2020-01-01", "2020-01-10");
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertSame(resp, result);
    }

    @Test
    void getTimeframeRates_whenRestTemplateThrows_shouldWrapInRuntimeException() throws Exception {
        RestTemplate mockRt = Mockito.mock(RestTemplate.class);
        Mockito.when(mockRt.getForObject(Mockito.anyString(), Mockito.eq(TimeframeRateResponse.class))).thenThrow(new RuntimeException("io error"));

        ExternalRateClient client = setupClient(mockRt);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> client.getTimeframeRates("USD", "2020-01-01", "2020-01-02"));
        assertTrue(ex.getMessage().contains("Failed to fetch external rates"));
    }
}
