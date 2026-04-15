package cz.tul.stin.backend.storage;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import cz.tul.stin.backend.service.RateClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CurrencyRepository {

    private final RateClient rateClient;
    private final Clock clock;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static class CacheEntry {
        TreeMap<String, Map<String, Double>> rates = new TreeMap<>();
        Map<String, Double> latestRates = null;
        boolean isUpToDate = false;
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public CurrencyRepository(RateClient rateClient, Clock clock) {
        this.rateClient = rateClient;
        this.clock = clock;
    }

    public Map<String, Double> getLatestRates(String base) {
        CacheEntry entry = getOrFetchCacheEntry(base);

        if (entry.latestRates == null || entry.latestRates.isEmpty()) {
            throw new RuntimeException("Latest rates absent in this entry.");
        }

        return entry.latestRates;
    }

    public TreeMap<String, Map<String, Double>> getTimeframeRates(String base, String startDate, String endDate) {
        CacheEntry entry = getOrFetchCacheEntry(base);
        SortedMap<String, Map<String, Double>> rates = entry.rates.subMap(startDate, true, endDate, true);
        return new TreeMap<>(rates);
    }

    private CacheEntry getOrFetchCacheEntry(String base) {
        CacheEntry entry = cache.computeIfAbsent(base, k -> new CacheEntry());

        if (!entry.isUpToDate) {
            String endDate = LocalDate.now(clock).format(DATE_FORMATTER);
            String startDate = LocalDate.now(clock).minusYears(1).format(DATE_FORMATTER);
            TimeframeRateResponse response = rateClient.getTimeframeRates(base,  startDate, endDate);

            if (response != null && response.getQuotes() != null && !response.getQuotes().isEmpty()) {
                entry.rates.clear();
                
                // Transform keys: EURCZK -> CZK
                for (Map.Entry<String, Map<String, Double>> dateEntry : response.getQuotes().entrySet()) {
                    Map<String, Double> originalRates = dateEntry.getValue();
                    Map<String, Double> transformedRates = new HashMap<>();
                    
                    for (Map.Entry<String, Double> rateEntry : originalRates.entrySet()) {
                        String currencyCode = rateEntry.getKey();
                        if (currencyCode.startsWith(base) && currencyCode.length() > base.length()) {
                            currencyCode = currencyCode.substring(base.length());
                        }
                        transformedRates.put(currencyCode, rateEntry.getValue());
                    }
                    entry.rates.put(dateEntry.getKey(), transformedRates);
                }

                entry.latestRates = entry.rates.lastEntry().getValue();
                entry.isUpToDate = true;
            } else {
                throw new RuntimeException("API did not return an entry for this base currency: " + base);
            }
        }

        return entry;
    }

    @Scheduled(cron = "0 30 0 * * *")
    public void invalidateCache() {
        for (CacheEntry entry : cache.values()) {
            entry.isUpToDate = false;
        }
    }
}
