package cz.tul.stin.backend.config;

import cz.tul.stin.backend.model.CurrencyExtremes;
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
        var client = new MockRateClient();
        calculator = new RateCalculatorService(client);
        CurrencyExtremes extremes = calculator.getExtremes("EUR");
        System.out.printf("Strongest: %s (%f)%n", extremes.strongest().getKey() ,extremes.strongest().getValue());
        System.out.printf("Weakest: %s (%f)%n", extremes.weakest().getKey() ,extremes.weakest().getValue());
    }
}
