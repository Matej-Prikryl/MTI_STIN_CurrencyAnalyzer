package cz.tul.stin.backend.config;

import cz.tul.stin.backend.service.RateClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class StartupRunner implements CommandLineRunner {
    private final RateClient rateclient;

    public StartupRunner(RateClient rateclient) {
        this.rateclient = rateclient;
    }

    @Override
    public void run(String... args) throws Exception {
        var response = rateclient.getLiveRates("EUR");
        System.out.println(response.getBase());
    }
}
