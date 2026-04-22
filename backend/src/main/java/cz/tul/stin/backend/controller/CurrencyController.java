package cz.tul.stin.backend.controller;

import cz.tul.stin.backend.config.SupportedCurrenciesConfig;
import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.service.RateCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api/rates")
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
        CurrencyInfo info = rateCalculatorService.getCurrencyInfo(base, startDate, endDate, targetCurrencies);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/supported-currencies")
    public ResponseEntity<Set<String>> getSupportedCurrencies() {
        return ResponseEntity.ok(new TreeSet<>(SupportedCurrenciesConfig.SUPPORTED_CURRENCIES));
    }
}
