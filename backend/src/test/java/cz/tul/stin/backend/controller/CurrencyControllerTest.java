package cz.tul.stin.backend.controller;

import cz.tul.stin.backend.model.CurrencyExtremes;
import cz.tul.stin.backend.model.CurrencyInfo;
import cz.tul.stin.backend.service.RateCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateCalculatorService rateCalculatorService;

    @Test
    void testGetCurrencyInfo_Success() throws Exception {
        // Prepare mock data
        CurrencyExtremes.RateEntry strongest = new CurrencyExtremes.RateEntry("GBP", 0.85);
        CurrencyExtremes.RateEntry weakest = new CurrencyExtremes.RateEntry("CZK", 25.0);
        CurrencyExtremes extremes = new CurrencyExtremes(strongest, weakest);
        Map<String, Double> averages = Map.of("USD", 1.1, "CZK", 24.5);
        CurrencyInfo mockInfo = new CurrencyInfo(extremes, averages);

        when(rateCalculatorService.getCurrencyInfo(anyString(), anyString(), anyString(), anySet()))
                .thenReturn(mockInfo);

        mockMvc.perform(get("/api/rates/currencyinfo")
                        .param("base", "EUR")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .param("targetCurrencies", "USD,CZK,GBP")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averages.USD").value(1.1))
                .andExpect(jsonPath("$.averages.CZK").value(24.5))
                .andExpect(jsonPath("$.extremes.strongest.key").value("GBP"))
                .andExpect(jsonPath("$.extremes.strongest.value").value(0.85))
                .andExpect(jsonPath("$.extremes.weakest.key").value("CZK"))
                .andExpect(jsonPath("$.extremes.weakest.value").value(25.0));
    }
}
