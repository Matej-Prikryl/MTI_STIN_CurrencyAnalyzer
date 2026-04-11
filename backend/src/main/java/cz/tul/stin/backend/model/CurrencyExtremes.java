package cz.tul.stin.backend.model;

import java.util.Map;

public record CurrencyExtremes(
        Map.Entry<String, Double> strongest,
        Map.Entry<String, Double> weakest
) { }
