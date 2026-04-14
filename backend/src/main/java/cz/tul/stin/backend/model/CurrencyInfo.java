package cz.tul.stin.backend.model;

import java.util.Map;

public record CurrencyInfo(
        CurrencyExtremes extremes,
        Map<String, Double> averages
) { }
