package cz.tul.stin.backend.storage;

import cz.tul.stin.backend.model.TimeframeRateResponse;
import cz.tul.stin.backend.service.RateClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CurrencyRepository {

    private final RateClient rateClient;

    private static class CacheEntry {
        TreeMap<String, Map<String, Double>> rates = new TreeMap<>();
        Map<String, Double> latestRates = null;
        boolean isUpToDate = false;
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public CurrencyRepository(RateClient rateClient) {
        this.rateClient = rateClient;
    }

    public Map<String, Double> getLatestRates(String base) {
        CacheEntry entry = getOrFetchCacheEntry(base);

        if (entry.latestRates == null || entry.latestRates.isEmpty()) {
            throw new RuntimeException("Latest rates absent in this entry.");
        }

        return entry.latestRates;
    }

    public TreeMap<String, Map<String, Double>> getTimeframeRates(String base) {
        CacheEntry entry = getOrFetchCacheEntry(base);
        return entry.rates;
    }

    private CacheEntry getOrFetchCacheEntry(String base) {
        CacheEntry entry = cache.computeIfAbsent(base, k -> new CacheEntry());

        if (!entry.isUpToDate) {
            TimeframeRateResponse response = rateClient.getTimeframeRates(base);

            if (response != null && response.getQuotes() != null && !response.getQuotes().isEmpty()) {
                entry.rates.clear();
                entry.rates.putAll(response.getQuotes());

                entry.latestRates = entry.rates.lastEntry().getValue();
                entry.isUpToDate = true;
            } else {
                throw new RuntimeException("API did not return an entry for this base currency: " + base);
            }
        }

        return entry;
    }
}
