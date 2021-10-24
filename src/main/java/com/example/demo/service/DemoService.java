package com.example.demo.service;

import com.example.demo.domain.CoinChangeResponse;
import com.example.demo.domain.CoinRechargeRequest;
import com.example.demo.domain.CoinRechargeResponse;

public interface  DemoService {

    public CoinChangeResponse getChange(Integer bill, Boolean getMaximumCoins) throws Exception;

    public CoinRechargeResponse rechargeCoins(CoinRechargeRequest coinRechargeRequest) throws Exception;
}
