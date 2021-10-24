package com.example.demo.utility;

import com.example.demo.domain.Coin;
import com.example.demo.domain.CoinRechargeRequest;
import com.example.demo.domain.Error;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class Validator {

    private static final Logger logger = LoggerFactory.getLogger(Validator.class);
    @Autowired
    PropertySplitter propertySplitter;
    @Value("#{'${application.supported-bills}'.split(',')}")
    private List<Integer> supportedBills;
    @Value("#{'${application.supported-coins}'.split(',')}")
    private List<Double> supportedCoins;

    public Error validateGetCoinChangeRequest(String bill, String getMaximumCoins) throws Exception {
        Error error = null;
        List<String> errorList = new ArrayList<>();
        if (StringUtils.isBlank(bill)) {
            errorList.add("Bill not passed. Please pass a bill for which change is requested.");
        } else {
            try {
                Integer billValue = Integer.parseInt(bill);
                if (!supportedBills.contains(billValue))
                    errorList.add("Invalid Bill. Bill should be one of: " + supportedBills);

            } catch (Exception e) {
                errorList.add("Invalid Bill value");
            }
            /*if (StringUtils.isNotBlank(choiceOfCoins)) {
                Map<String, String> choiceOfCoinsMap = propertySplitter.map(choiceOfCoins);
                if (choiceOfCoinsMap.isEmpty()) {
                    errorList.add("Invalid choiceOfCoins. It should be passed as coin1:coin1Quantity,coin2:coin2Quantity, etc");
                } else {
                    try {
                        choiceOfCoinsMap.keySet().stream().forEach(s -> {
                            if (!supportedCoins.contains(Double.parseDouble(s))) {
                                errorList.add("Invalid choiceOfCoin value: " + s);
                            }
                            if (StringUtils.isBlank(choiceOfCoinsMap.get(s)) || Integer.parseInt(choiceOfCoinsMap.get(s)) < 0) {
                                errorList.add("Invalid choiceOfCoin quantity: " + s + ":" + choiceOfCoinsMap.get(s));
                            }
                        });
                    } catch (Exception e) {
                        errorList.add("Invalid choiceOfCoin value");
                    }
                }
            }*/
            if (StringUtils.isNotBlank(getMaximumCoins) && (!"true".equalsIgnoreCase(getMaximumCoins) && !"false".equalsIgnoreCase(getMaximumCoins))) {
                errorList.add("Invalid value for getMaximumCoins. The value should be either true or false.");
            }
        }
        if (errorList.size() > 0) {
            error = new Error();
            error.setErrors(errorList);
        }
        return error;
    }

    public Error validateCoinRechargeRequest(CoinRechargeRequest coinRechargeRequest) throws Exception {
        Error error = null;
        List<String> errorList = new ArrayList<>();
        if (null == coinRechargeRequest || CollectionUtils.isEmpty(coinRechargeRequest.getCoins())) {
            errorList.add("Invalid request. Please pass the coins and their quantity to be recharged");
        } else {
            Coin coin = null;
            for (int i = 0; i < coinRechargeRequest.getCoins().size(); i++) {
                coin = coinRechargeRequest.getCoins().get(i);
                if (null != coin) {
                    if (!supportedCoins.contains(coin.getCoinValue())) {
                        errorList.add(String.format("Invalid coinValue at index(%s). Passed coinValue:%s. It should be one of: %s", i, coin.getCoinValue(), supportedCoins));
                    }
                    if (coin.getQuantity() < 1) {
                        errorList.add(String.format("Invalid quantity at index(%s). Passed quantity:%s. It should be greater than zero.", i, coin.getQuantity()));
                    }
                } else {
                    errorList.add(String.format("Invalid coin at index(%s). Please pass the coin value and the quantity to be recharged", i));
                }
            }
        }
        if (errorList.size() > 0) {
            error = new Error();
            error.setErrors(errorList);
        }
        return error;
    }
}
