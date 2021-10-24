package com.example.demo.controller;

import com.example.demo.CoinChangeApplication;
import com.example.demo.Repository.DemoRepository;
import com.example.demo.config.ApiControllerAdvise;
import com.example.demo.domain.Coin;
import com.example.demo.domain.CoinRechargeRequest;
import com.example.demo.service.DemoServiceImpl;
import com.example.demo.utility.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CoinChangeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoControllerTest {
    private static Logger logger;
    private MockMvc mockMvc;

    private String getCoinChangePath = "/bills/%s/coinChange";

    private String putCoinRechargePath = "/coins/recharges";

    @InjectMocks
    private DemoController demoController;

    @InjectMocks
    private DemoServiceImpl demoService;

    @InjectMocks
    private Validator validator;

    @Mock
    private DemoRepository demoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("#{'${application.supported-bills}'.split(',')}")
    private List<Integer> supportedBills;

    @Value("#{'${application.supported-coins}'.split(',')}")
    private List<Double> supportedCoins;

    @Value("#{PropertySplitter.map('${application.available-coins-map}')}")
    private Map<Double, Integer> availableCoinsMap = new HashMap<>();

    @Autowired
    private ApiControllerAdvise apiControllerAdvise;

    @Autowired
    private MappingJackson2HttpMessageConverter jackson;

    @Before
    public void beforeClass() {
        logger = LoggerFactory.getLogger(DemoControllerTest.class);
    }

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Mock controller and its services
        ReflectionTestUtils.setField(demoController, "demoService", demoService);
        ReflectionTestUtils.setField(demoController, "validator", validator);
        ReflectionTestUtils.setField(validator, "supportedBills", supportedBills);
        ReflectionTestUtils.setField(validator, "supportedCoins", supportedCoins);
        ReflectionTestUtils.setField(demoService, "supportedCoins", supportedCoins);
        ReflectionTestUtils.setField(demoService, "demoRepository", demoRepository);
        ReflectionTestUtils.setField(demoRepository, "availableCoinsMap", availableCoinsMap);
        mockMvc = standaloneSetup(demoController).setControllerAdvice(apiControllerAdvise).setMessageConverters(jackson).build();
    }

    @Test
    public void validationErrorForInvalidBillValue() throws Exception {
        ResultMatcher resultMatcher = jsonPath("$.errors[0]").value("Invalid Bill. Bill should be one of: [1, 2, 5, 10, 20, 50, 100]");
        String url = String.format(getCoinChangePath, "3");
        mockMvc.perform(get(url)).andExpect(status().isBadRequest()).andExpect(resultMatcher);
    }

    @Test
    public void validationErrorForNoBillValue() throws Exception {
        ResultMatcher resultMatcher = jsonPath("$.errors[0]").value("Bill not passed. Please pass a bill for which change is requested.");
        String url = String.format(getCoinChangePath, " ");
        mockMvc.perform(get(url)).andExpect(status().isBadRequest()).andExpect(resultMatcher);
    }

    @Test
    public void validationErrorForInvalidGetMaximumCoins() throws Exception {
        ResultMatcher resultMatcher = jsonPath("$.errors[0]").value("Invalid Bill. Bill should be one of: [1, 2, 5, 10, 20, 50, 100]");
        ResultMatcher resultMatcher1 = jsonPath("$.errors[1]").value("Invalid value for getMaximumCoins. The value should be either true or false.");
        String url = String.format(getCoinChangePath, "3") + "?getMaximumCoins=trued";
        mockMvc.perform(get(url)).andExpect(status().isBadRequest()).andExpect(resultMatcher).andExpect(resultMatcher1);
    }

    @Test
    public void okFor10BillWithNoValueForGetMaximumCoins() throws Exception {
        when(demoRepository.getAvailableQuantity(anyDouble())).thenReturn(40);
        ResultMatcher resultMatcher = jsonPath("$.coins[0].coinValue").value("0.25");
        ResultMatcher resultMatcher1 = jsonPath("$.coins[0].quantity").value("40");
        String url = String.format(getCoinChangePath, "10");
        mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(resultMatcher).andExpect(resultMatcher1);
    }

    @Test
    public void okFor10BillWithFalseForGetMaximumCoins() throws Exception {
        when(demoRepository.getAvailableQuantity(anyDouble())).thenReturn(40);
        ResultMatcher resultMatcher = jsonPath("$.coins[0].coinValue").value("0.25");
        ResultMatcher resultMatcher1 = jsonPath("$.coins[0].quantity").value("40");
        String url = String.format(getCoinChangePath, "10") + "?getMaximumCoins=false";
        mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(resultMatcher).andExpect(resultMatcher1);
    }

    @Test
    public void okFor10BillWithTrueGetMaximumCoins() throws Exception {
        when(demoRepository.getAvailableQuantity(anyDouble())).thenReturn(100);
        ResultMatcher resultMatcher = jsonPath("$.coins[0].coinValue").value("0.01");
        ResultMatcher resultMatcher1 = jsonPath("$.coins[0].quantity").value("100");
        String url = String.format(getCoinChangePath, "10") + "?getMaximumCoins=true";
        mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(resultMatcher).andExpect(resultMatcher1);
    }

    @Test
    public void ErrorFor10BillWithNotEnoughCoin() throws Exception {
        when(demoRepository.getAvailableQuantity(anyDouble())).thenReturn(4);
        ResultMatcher resultMatcher = jsonPath("$.errorMessage").value("There is not enough coin change available for the requested bill. Please try a smaller bill.");
        String url = String.format(getCoinChangePath, "10") + "?getMaximumCoins=false";
        mockMvc.perform(get(url)).andExpect(status().isFailedDependency()).andExpect(resultMatcher);
    }

    @Test
    public void okFor1Bill() throws Exception {
        when(demoRepository.getAvailableQuantity(anyDouble())).thenReturn(100);
        ResultMatcher resultMatcher = jsonPath("$.coins[0].coinValue").value("0.01");
        ResultMatcher resultMatcher1 = jsonPath("$.coins[0].quantity").value("100");
        String url = String.format(getCoinChangePath, "1") + "?getMaximumCoins=true";
        mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(resultMatcher).andExpect(resultMatcher1);
    }

    @Test
    public void ErrorForNoCoinsAvailable() throws Exception {
        when(demoRepository.getAvailableQuantity(anyDouble())).thenReturn(0);
        ResultMatcher resultMatcher = jsonPath("$.errorMessage").value("No coin change available. Please try again later.");
        String url = String.format(getCoinChangePath, "10") + "?getMaximumCoins=false";
        mockMvc.perform(get(url)).andExpect(status().isFailedDependency()).andExpect(resultMatcher);
    }

    @Test
    public void okForPutCoinsRecharge() throws Exception {
        ResultMatcher resultMatcher = jsonPath("$.coins[*].coinValue").value(supportedCoins);
        mockMvc.perform(put(putCoinRechargePath).contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsBytes(coinRechargeRequest())))
                .andExpect(status().isOk()).andExpect(resultMatcher);
    }

    @Test
    public void validationErrorForPutCoinsRechargeInvalidCoinValue() throws Exception {
        ResultMatcher resultMatcher = jsonPath("$.errors[0]").value("Invalid coinValue at index(0). Passed coinValue:0.2. It should be one of: [0.01, 0.05, 0.1, 0.25]");
        ResultMatcher resultMatcher1 = jsonPath("$.errors[1]").value("Invalid quantity at index(0). Passed quantity:-1. It should be greater than zero.");
        mockMvc.perform(put(putCoinRechargePath).contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsBytes(invalidCoinRechargeRequest())))
                .andExpect(status().isBadRequest()).andExpect(resultMatcher).andExpect(resultMatcher1);
    }

    @Test
    public void getException() throws Exception {
        when(demoService.getChange(10,false)).thenThrow(new RuntimeException());
        ResultMatcher resultMatcher = jsonPath("$.errors[0]").value("Error in application. Please try again later.");
        String url = String.format(getCoinChangePath, "10") + "?getMaximumCoins=false";
        mockMvc.perform(get(url)).andExpect(status().isInternalServerError()).andExpect(resultMatcher);
    }

    private CoinRechargeRequest coinRechargeRequest(){
        CoinRechargeRequest coinRechargeRequest = new CoinRechargeRequest();
        List<Coin> coinList = new ArrayList<>();
        Coin coin = new Coin();
        coin.setCoinValue(0.25);
        coin.setQuantity(100);
        coinList.add(coin);

        coin = new Coin();
        coin.setCoinValue(0.1);
        coin.setQuantity(100);
        coinList.add(coin);

        coin = new Coin();
        coin.setCoinValue(0.05);
        coin.setQuantity(100);
        coinList.add(coin);

        coin = new Coin();
        coin.setCoinValue(0.01);
        coin.setQuantity(100);
        coinList.add(coin);

        coinRechargeRequest.setCoins(coinList);

        return coinRechargeRequest;
    }

    private CoinRechargeRequest invalidCoinRechargeRequest(){
        CoinRechargeRequest coinRechargeRequest = new CoinRechargeRequest();
        List<Coin> coinList = new ArrayList<>();
        Coin coin = new Coin();
        coin.setCoinValue(0.20);
        coin.setQuantity(-1);
        coinList.add(coin);

        coinRechargeRequest.setCoins(coinList);

        return coinRechargeRequest;
    }
}

