package cz.tul.stin.backend.model;

public record CurrencyExtremes(
        RateEntry strongest,
        RateEntry weakest
) {
    public record RateEntry(String key, Double value) {}
}
