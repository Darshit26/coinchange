package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoinChangeResponse {

    private Integer bill;

    private List<Coin> coins;

    private String errorMessage;

    public Integer getBill() {
        return bill;
    }

    public void setBill(Integer bill) {
        this.bill = bill;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public void setCoins(List<Coin> coins) {
        this.coins = coins;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoinChangeResponse)) return false;
        CoinChangeResponse that = (CoinChangeResponse) o;
        return bill.equals(that.bill) && Objects.equals(coins, that.coins) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bill, coins, errorMessage);
    }

    @Override
    public String toString() {
        return "CoinChangeResponse{" +
                "bill=" + bill +
                ", coins=" + coins +
                ", errorMessage=" + errorMessage +
                '}';
    }

}
