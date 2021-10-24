package com.example.demo.domain;

import java.util.List;
import java.util.Objects;

public class CoinRechargeRequest {

    private List<Coin> coins;

    public List<Coin> getCoins() {
        return coins;
    }

    public void setCoins(List<Coin> coins) {
        this.coins = coins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoinRechargeRequest)) return false;
        CoinRechargeRequest that = (CoinRechargeRequest) o;
        return Objects.equals(coins, that.coins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coins);
    }

    @Override
    public String toString() {
        return "coinRechargeRequest{" +
                "coins=" + coins +
                '}';
    }

}
