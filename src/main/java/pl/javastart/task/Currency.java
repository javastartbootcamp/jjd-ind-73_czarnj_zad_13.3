package pl.javastart.task;

import java.math.BigDecimal;

public class Currency {
    private String name;
    private BigDecimal currencyRate;

    public Currency(String name, BigDecimal currencyRate) {
        this.name = name;
        this.currencyRate = currencyRate;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCurrencyRate() {
        return currencyRate;
    }
}
