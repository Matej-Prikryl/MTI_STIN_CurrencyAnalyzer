package cz.tul.stin.backend.controller;

import cz.tul.stin.backend.config.SupportedCurrenciesConfig;
import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.service.RateCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api/rates")
@Slf4j
public class CurrencyController {
    private final RateCalculatorService rateCalculatorService;

    public CurrencyController(RateCalculatorService rateCalculatorService) {
        this.rateCalculatorService = rateCalculatorService;
    }

    @GetMapping("/currencyinfo")
    public ResponseEntity<CurrencyInfo> getCurrencyInfo(
            @RequestParam String base,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Set<String> targetCurrencies
            ) {
        log.debug("Fetching currency info for Base: {}, Start Date: {}, End Date: {}, Target Currencies: {}",
                base, startDate, endDate, targetCurrencies);
        CurrencyInfo info = rateCalculatorService.getCurrencyInfo(base, startDate, endDate, targetCurrencies);
        var response = ResponseEntity.ok(info);
        log.debug("Response: {}", response);
        return response;
    }

    @GetMapping("/supported-currencies")
    public ResponseEntity<Set<String>> getSupportedCurrencies() {
        log.debug("Fetching supported currencies");
        return ResponseEntity.ok(new TreeSet<>(SupportedCurrenciesConfig.SUPPORTED_CURRENCIES));
    }
}
