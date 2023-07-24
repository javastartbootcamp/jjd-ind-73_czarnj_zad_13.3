package pl.javastart.task;

import java.math.BigDecimal;

public class Product {
    private String name;
    private BigDecimal price;
    private String currency;

    public Product(String name, BigDecimal price, String currency) {
        this.name = name;
        this.price = price;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
