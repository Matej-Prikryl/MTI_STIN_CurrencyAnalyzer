package cz.tul.stin.backend.config;

import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.service.MockRateClient;
import cz.tul.stin.backend.service.RateCalculatorService;
import cz.tul.stin.backend.service.RateClient;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        calculator = new RateCalculatorService(client);
        CurrencyInfo info = calculator.getCurrencyInfo("EUR");
        System.out.printf("Strongest: %s (%f)%n", info.extremes().strongest().getKey() ,info.extremes().strongest().getValue());
        System.out.printf("Weakest: %s (%f)%n", info.extremes().weakest().getKey() ,info.extremes().weakest().getValue());
        System.out.println("Averages:");
        for (String currency : info.averages().keySet()) {
            System.out.printf("%s: %f%n", currency, info.averages().get(currency));
        }
    }
}
