package cz.tul.stin.backend.config;

import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.service.MockRateClient;
import cz.tul.stin.backend.service.RateCalculatorService;
import cz.tul.stin.backend.storage.CurrencyRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("dev")
public class StartupRunner implements CommandLineRunner {
    private RateCalculatorService calculator;

    public StartupRunner(RateCalculatorService calculator) {
        this.calculator = calculator;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        System.out.println("Starting...");

        var client = new MockRateClient();
        Clock clock = Clock.fixed(Instant.parse("2011-01-01T10:00:00Z"), ZoneId.of("UTC"));
        var repository = new CurrencyRepository(client, clock);
        calculator = new RateCalculatorService(repository);

        Set<String> targetCurrencies = new HashSet<>();
        targetCurrencies.add("USD");
        targetCurrencies.add("EUR");
        targetCurrencies.add("GBP");
        targetCurrencies.add("CZK");

        CurrencyInfo info = calculator.getCurrencyInfo("EUR", "2010-03-01", "2010-03-02",  targetCurrencies);
        System.out.printf("Strongest: %s (%f)%n", info.extremes().strongest().key() ,info.extremes().strongest().value());
        System.out.printf("Weakest: %s (%f)%n", info.extremes().weakest().key() ,info.extremes().weakest().value());
        System.out.println("Averages:");
        for (String currency : info.averages().keySet()) {
            System.out.printf("%s: %f%n", currency, info.averages().get(currency));
        }
    }
}
