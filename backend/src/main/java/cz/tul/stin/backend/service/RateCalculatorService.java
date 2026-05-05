package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.storage.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
@Slf4j
public class RateCalculatorService {
    private final CurrencyRepository currencyRepository;

    public RateCalculatorService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public CurrencyInfo getCurrencyInfo(String base, String startDate, String endDate, Set<String> targetCurrencies) {
        log.info("Calculating currency info for base: {} from {} to {}. Targets: {}", base, startDate, endDate, targetCurrencies);
        var latestRates = currencyRepository.getLatestRates(base);
        var rates = currencyRepository.getTimeframeRates(base, startDate, endDate);

        log.debug("Latest rates received: {} entries. Timeframe rates received: {} days.",
                (latestRates != null ? latestRates.size() : 0),
                (rates != null ? rates.size() : 0));

        var extremes = getExtremes(latestRates, targetCurrencies);
        var averages = getAverageRates(rates, targetCurrencies);

        log.info("Calculation completed successfully for base currency: {}", base);
        return new CurrencyInfo(extremes, averages);
    }

    private Map<String, Double> getAverageRates(TreeMap<String, Map<String, Double>> rates , Set<String> targetCurrencies) {
        if (rates == null || rates.isEmpty()) {
            throw new IllegalArgumentException("No data available for the given timeframe.");
        }

        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        for (Map.Entry<String, Map<String, Double>> dateEntry : rates.entrySet()) {
            Map<String, Double> dailyRates = dateEntry.getValue();
            if (dailyRates == null) {
                log.warn("Skipping null daily rate entry for date: {}", dateEntry.getKey());
                continue;
            }

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

        log.debug("Extremes found - Strongest: {} ({}), Weakest: {} ({})",
                strongest.getKey(), strongest.getValue(), weakest.getKey(), weakest.getValue());

        return new CurrencyExtremes(
                new CurrencyExtremes.RateEntry(strongest.getKey(), strongest.getValue()),
                new CurrencyExtremes.RateEntry(weakest.getKey(), weakest.getValue())
        );
    }
}
