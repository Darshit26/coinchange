package com.example.demo.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DemoRepository {

    private static final Logger logger = LoggerFactory.getLogger(DemoRepository.class);

    @Value("#{PropertySplitter.map('${application.available-coins-map}')}")
    private Map<Double, Integer> availableCoinsMap = new HashMap<>();

    public Integer getAvailableQuantity(Double coinValue) {
        return availableCoinsMap.getOrDefault(coinValue, 0);
    }

    public void updateAvailableQuantity(Double coinValue, Integer quantity) {
        availableCoinsMap.put(coinValue, quantity);
    }
}

