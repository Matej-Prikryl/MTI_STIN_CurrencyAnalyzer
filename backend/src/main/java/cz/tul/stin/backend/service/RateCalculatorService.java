package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.model.TimeframeRateResponse;
import cz.tul.stin.backend.storage.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RateCalculatorService {
    private final CurrencyRepository currencyRepository;

    public RateCalculatorService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public CurrencyInfo getCurrencyInfo(String base, String startDate, String endDate) {
        var latestRates = currencyRepository.getLatestRates(base);
        var rates = currencyRepository.getTimeframeRates(base, startDate, endDate);
        var extremes = getExtremes(latestRates);
        var averages = getAverageRates(rates);
        return new CurrencyInfo(extremes, averages);
    }

    private Map<String, Double> getAverageRates(TreeMap<String, Map<String, Double>> rates) {
        if (rates == null || rates.isEmpty()) {
            throw new IllegalArgumentException("No data available for the given timeframe.");
        }

        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (Map<String, Double> dailyRates : rates.values()) {
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

    private CurrencyExtremes getExtremes(Map<String, Double> latestRates) {
        if (latestRates == null || latestRates.isEmpty()) {
            throw new RuntimeException("Error: No rates to compare.");
        }

        var strongest = latestRates.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get();

        var weakest = latestRates.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get();

        return new CurrencyExtremes(strongest, weakest);
    }
}
