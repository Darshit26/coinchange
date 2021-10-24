package com.example.demo.domain;

import java.util.Objects;

public class Coin {

    private Double coinValue;

    private Integer quantity;

    public Double getCoinValue() {
        return coinValue;
    }

    public void setCoinValue(Double coinValue) {
        this.coinValue = coinValue;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coin)) return false;
        Coin that = (Coin) o;
        return Objects.equals(coinValue, that.coinValue) && Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coinValue, quantity);
    }

    @Override
    public String toString() {
        return "Coin{" +
                "coin=" + coinValue +
                ", quantity=" + quantity +
                '}';
    }

}
