package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ExternalRateClientTest {

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void getTimeframeRates_success_callsRestTemplateWithExpectedUrl() throws Exception {
        ExternalRateClient client = new ExternalRateClient();

        // set apiUrl and apiKey
        setPrivateField(client, "apiUrl", "http://api.test");
        setPrivateField(client, "apiKey", "MYKEY");

        // mock RestTemplate and response
        RestTemplate mockRt = Mockito.mock(RestTemplate.class);
        TimeframeRateResponse resp = new TimeframeRateResponse();
        resp.setSuccess(true);
        Mockito.when(mockRt.getForObject(Mockito.anyString(), Mockito.eq(TimeframeRateResponse.class))).thenReturn(resp);
        
        // replace internal restTemplate
        Field rtField = ExternalRateClient.class.getDeclaredField("restTemplate");
        rtField.setAccessible(true);
        rtField.set(client, mockRt);
        // call
        TimeframeRateResponse result = client.getTimeframeRates("EUR", "2020-01-01", "2020-01-10");
        // verify
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockRt).getForObject(captor.capture(), Mockito.eq(TimeframeRateResponse.class));
        String url = captor.getValue();
        assertTrue(url.startsWith("http://api.test"));                assertTrue(url.contains("access_key=MYKEY"));        assertTrue(url.contains("source=EUR"));        assertTrue(url.contains("start_date=2020-01-01"));        assertTrue(url.contains("end_date=2020-01-10"));        assertTrue(url.contains("currencies="));        // SUPPORTED_CURRENCIES contains USD and CZK among others        assertTrue(url.contains("USD") || url.contains("CZK"));        assertSame(resp, result);    }

    @Test
    void getTimeframeRates_whenRestTemplateThrows_shouldWrapInRuntimeException() throws Exception {
        ExternalRateClient client = new ExternalRateClient();
        setPrivateField(client, "apiUrl", "http://api.test");
        setPrivateField(client, "apiKey", "K");
        RestTemplate mockRt = Mockito.mock(RestTemplate.class);        Mockito.when(mockRt.getForObject(Mockito.anyString(), Mockito.eq(TimeframeRateResponse.class))).thenThrow(new RuntimeException("io error"));
        Field rtField = ExternalRateClient.class.getDeclaredField("restTemplate");        rtField.setAccessible(true);        rtField.set(client, mockRt);        RuntimeException ex = assertThrows(RuntimeException.class, () -> client.getTimeframeRates("USD", "2020-01-01", "2020-01-02"));        assertTrue(ex.getMessage().contains("Failed to fetch external rates"));    }
}
