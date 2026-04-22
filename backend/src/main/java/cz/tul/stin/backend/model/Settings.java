package cz.tul.stin.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Settings {
    private String language;
    private String baseCurrency;
    private Set<String> preferredCurrencies;

    public Settings() {
        this.language = "en";
        this.baseCurrency = "EUR";
        this.preferredCurrencies = new HashSet<>();
        this.preferredCurrencies.add("USD");
        this.preferredCurrencies.add("EUR");
        this.preferredCurrencies.add("GBP");
        this.preferredCurrencies.add("CZK");
    }
}
