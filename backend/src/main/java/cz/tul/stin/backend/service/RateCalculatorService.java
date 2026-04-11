package cz.tul.stin.backend.service;

import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.model.LiveRateResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RateCalculatorService {
    private final RateClient rateClient;

    public RateCalculatorService(RateClient client) {
        this.rateClient = client;
    }

    public CurrencyExtremes getExtremes(String base) {
        LiveRateResponse response = rateClient.getLiveRates(base);
        Map<String, Double> rates = response.getQuotes();

        var strongest = rates.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get();

        var weakest = rates.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get();

        return new CurrencyExtremes(strongest, weakest);
    }
}
