package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
@Slf4j
//@Primary
public class ExternalRateClient implements RateClient {
    @Value("${api.exchangerate.url}")
    private String apiUrl;

    @Value("${api.exchangerate.key}")
    private String apiKey;

    public static final Set<String> SUPPORTED_CURRENCIES = Set.of(
                    "AUD", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD", "HUF",
                    "ILS", "JPY", "KRW", "MXN", "NOK", "NZD", "PLN", "RON", "SEK", "SGD",
                    "THB", "TRY", "USD", "ZAR"
    );

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TimeframeRateResponse getTimeframeRates(String base, String startDate, String endDate) {
        String currencies = String.join(",", SUPPORTED_CURRENCIES);
        String url = String.format("%s?access_key=%s&source=%s&currencies=%s&start_date=%s&end_date=%s",
                apiUrl, apiKey, base, currencies, startDate, endDate);

        try {
            return restTemplate.getForObject(url, TimeframeRateResponse.class);
        }  catch (Exception e) {
            log.error("Failed to fetch external rates: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch external rates: " + e.getMessage());
        }
    }
}
