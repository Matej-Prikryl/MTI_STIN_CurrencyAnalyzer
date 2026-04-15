package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.storage.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class RateCalculatorService {
    private final CurrencyRepository currencyRepository;

    public RateCalculatorService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public CurrencyInfo getCurrencyInfo(String base, String startDate, String endDate, Set<String> targetCurrencies) {
        var latestRates = currencyRepository.getLatestRates(base);
        var rates = currencyRepository.getTimeframeRates(base, startDate, endDate);
        var extremes = getExtremes(latestRates, targetCurrencies);
        var averages = getAverageRates(rates, targetCurrencies);
        return new CurrencyInfo(extremes, averages);
    }

    private Map<String, Double> getAverageRates(TreeMap<String, Map<String, Double>> rates , Set<String> targetCurrencies) {
        if (rates == null || rates.isEmpty()) {
            throw new IllegalArgumentException("No data available for the given timeframe.");
        }

        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (Map<String, Double> dailyRates : rates.values()) {
            if (dailyRates == null) continue;

            for (Map.Entry<String, Double> entry : dailyRates.entrySet()) {
                String currency = entry.getKey();
                if (!targetCurrencies.contains(currency)) continue;
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

    private CurrencyExtremes getExtremes(Map<String, Double> latestRates, Set<String> targetCurrencies) {
        if (latestRates == null || latestRates.isEmpty()) {
            throw new RuntimeException("Error: No rates to compare.");
        }

        var filteredList = latestRates.entrySet().stream()
                .filter(entry -> targetCurrencies.contains(entry.getKey()))
                .toList();

        if (filteredList.isEmpty()) {
            throw new RuntimeException("Error: No rates in the filtered list.");
        }

        Map.Entry<String, Double> strongest = filteredList.stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow();

        Map.Entry<String, Double> weakest = filteredList.stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        return new CurrencyExtremes(
                new CurrencyExtremes.RateEntry(strongest.getKey(), strongest.getValue()),
                new CurrencyExtremes.RateEntry(weakest.getKey(), weakest.getValue())
        );
    }
}
