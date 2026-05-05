package cz.tul.stin.backend.service;

import cz.tul.stin.backend.config.SupportedCurrenciesConfig;
import cz.tul.stin.backend.model.TimeframeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Profile("!dev")
public class ExternalRateClient implements RateClient {
    @Value("${api.exchangerate.url}")
    private String apiUrl;

    @Value("${api.exchangerate.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TimeframeRateResponse getTimeframeRates(String base, String startDate, String endDate) {
        String currencies = String.join(",", SupportedCurrenciesConfig.SUPPORTED_CURRENCIES);
        String url = String.format("%s?access_key=%s&source=%s&currencies=%s&start_date=%s&end_date=%s",
                apiUrl, apiKey, base, currencies, startDate, endDate);

        log.debug("Requesting external rates from: {}?access_key=***&source={}&currencies={}&start_date={}&end_date={}",
                apiUrl, base, currencies, startDate, endDate);

        log.info("Fetching timeframe rates from external API for base: {} ({} to {})", base, startDate, endDate);

        try {
            TimeframeRateResponse response = restTemplate.getForObject(url, TimeframeRateResponse.class);

            if (response == null) {
                log.warn("External API returned null response for base: {}", base);
            } else if (!response.isSuccess()) {
                log.warn("External API request was not successful. Base: {}, Response Info: {}", base, response);
            } else {
                log.debug("Successfully received rates for {} days.",
                        (response.getQuotes() != null ? response.getQuotes().size() : 0));
            }

            return response;

        } catch (Exception e) {
            log.error("Critical error while calling external rate API: {}", e.getMessage());
            log.debug("Stack trace for API failure: ", e);
            throw new RuntimeException("Failed to fetch external rates: " + e.getMessage(), e);
        }
    }
}
