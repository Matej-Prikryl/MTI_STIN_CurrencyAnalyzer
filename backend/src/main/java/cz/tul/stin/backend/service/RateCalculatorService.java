package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.model.TimeframeRateResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateCalculatorService {
    private final RateClient rateClient;

    public RateCalculatorService(RateClient client) {
        this.rateClient = client;
    }

    public CurrencyInfo getCurrencyInfo(String base) {
        TimeframeRateResponse response = rateClient.getTimeframeRates(base);
        var extremes = getExtremes(response);
        var averages = getAverageRates(response);
        return new CurrencyInfo(extremes, averages);
    }

    private Map<String, Double> getAverageRates(TimeframeRateResponse response) {
        Map<String, Map<String, Double>> allRates = response.getQuotes();

        if (allRates == null || allRates.isEmpty()) {
            throw new IllegalArgumentException("No data available for the given timeframe.");
        }

        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (Map<String, Double> dailyRates : allRates.values()) {
            if (dailyRates == null) continue;

            for (Map.Entry<String, Double> entry : dailyRates.entrySet()) {
                String currency = entry.getKey();
                Double rate = entry.getValue();

                sums.put(currency, sums.getOrDefault(currency, 0.0) + rate);
                counts.put(currency, counts.getOrDefault(currency, 0) + 1);
            }
        }

        Map<String, Double> averages = new HashMap<>();
        for (String currency : sums.keySet()) {
            averages.put(currency, sums.get(currency) / counts.get(currency));
        }

        return averages;
    }

    private CurrencyExtremes getExtremes(TimeframeRateResponse response) {
        var rates = response.getLatestQuotes();
        if (rates == null || rates.isEmpty()) {
            throw new RuntimeException("Error: No rates to compare.");
        }

        var strongest = rates.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get();

        var weakest = rates.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get();

        return new CurrencyExtremes(strongest, weakest);
    }
}
