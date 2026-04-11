package cz.tul.stin.backend.config;

import cz.tul.stin.backend.service.RateClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("dev")
public class StartupRunner implements CommandLineRunner {
    private final RateClient rateClient;

    public StartupRunner(RateClient rateclient) {
        this.rateClient = rateclient;
    }

    @Override
    public void run(String... args) throws Exception {
        var response = rateClient.getLiveRates("EUR");
        Map<String, Double> quotes = response.getQuotes();
        System.out.println(quotes.get("EURCZK"));
    }
}
