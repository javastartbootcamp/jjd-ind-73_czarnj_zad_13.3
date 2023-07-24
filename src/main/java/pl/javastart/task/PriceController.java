package pl.javastart.task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PriceController {
    private static final int ROUNDING_SCALE = 10;

    public static List<Product> readProducts(String fileName) {
        List<Product> products = new ArrayList<>();
        try (var br = new BufferedReader(new FileReader(fileName))) {
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                Product product = parseProduct(nextLine);
                products.add(product);
            }
            return products;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static Product parseProduct(String line) {
        String[] lineAsArray = line.split(";");
        String name = lineAsArray[0];
        BigDecimal price = new BigDecimal(lineAsArray[1]);
        String currency = lineAsArray[2];
        return new Product(name, price, currency);
    }

    public static List<Currency> readCurrencies(String fileName) {
        List<Currency> currencies = new ArrayList<>();
        try (var br = new BufferedReader(new FileReader(fileName))) {
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                Currency currency = parseCurrency(nextLine);
                currencies.add(currency);
            }
            return currencies;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static Currency parseCurrency(String line) {
        String[] lineAsArray = line.split(";");
        String name = lineAsArray[0];
        BigDecimal rate = new BigDecimal(lineAsArray[1]);
        return new Currency(name, rate);
    }

    public static BigDecimal getSumInEuro(List<Product> products, List<Currency> currencies) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Product product : products) {
            BigDecimal rate = findRate(product.getCurrency(), currencies);
            BigDecimal euroPrice = calculatePriceInEuro(product.getPrice(), rate);
            sum = sum.add(euroPrice);
        }
        return sum;
    }

    public static BigDecimal getAveragePriceInEuro(List<Product> products, List<Currency> currencies) {
        BigDecimal sum = getSumInEuro(products, currencies);
        return sum.divide(new BigDecimal(products.size()), ROUNDING_SCALE, RoundingMode.UP);
    }

    public static Product getMostValuableProduct(List<Product> products, List<Currency> currencies) {
        Product mostValuableProduct = products.get(0);
        BigDecimal mostValuableProductEuroPrice = calculatePriceInEuro(mostValuableProduct.getPrice(),
                findRate(mostValuableProduct.getCurrency(), currencies));
        for (int i = 1; i < products.size(); i++) {
            Product product = products.get(i);
            BigDecimal euroPrice = calculatePriceInEuro(product.getPrice(), findRate(product.getCurrency(), currencies));
            if (euroPrice.compareTo(mostValuableProductEuroPrice) > 0) {
                mostValuableProduct = product;
                mostValuableProductEuroPrice = euroPrice;
            }
        }
        return mostValuableProduct;
    }

    public static Product getLeastValuableProduct(List<Product> products, List<Currency> currencies) {
        Product leastValuableProduct = products.get(0);
        BigDecimal leastValuableProductEuroPrice = calculatePriceInEuro(leastValuableProduct.getPrice(),
                findRate(leastValuableProduct.getCurrency(), currencies));
        for (int i = 1; i < products.size(); i++) {
            Product product = products.get(i);
            BigDecimal euroPrice = calculatePriceInEuro(product.getPrice(), findRate(product.getCurrency(), currencies));
            if (euroPrice.compareTo(leastValuableProductEuroPrice) < 0) {
                leastValuableProduct = product;
                leastValuableProductEuroPrice = euroPrice;
            }
        }
        return leastValuableProduct;
    }

    public static BigDecimal findRate(String currency, List<Currency> currencies) {
        for (Currency value : currencies) {
            if (value.getName().equals(currency)) {
                return value.getCurrencyRate();
            }
        }
        throw new UnknownCurrencyException("Brak kursu dla waluty " + currency);
    }

    public static void showStatistics(String productsFileName, String currenciesFileName) {
        try {
            List<Product> products = readProducts(productsFileName);
            List<Currency> currencies = readCurrencies(currenciesFileName);
            Product mostValuableProduct = getMostValuableProduct(products, currencies);
            BigDecimal rateForMostValuableProduct = findRate(mostValuableProduct.getCurrency(),
                    currencies);
            Product leastValuableProduct = getLeastValuableProduct(products, currencies);
            BigDecimal rateForLeastValuableProduct = findRate(leastValuableProduct.getCurrency(),
                    currencies);

            System.out.printf("Suma wszystkich produktów: %s EUR%n", getSumInEuro(products, currencies));
            System.out.printf("Średnia wartość produktu: %s EUR%n",
                    getAveragePriceInEuro(products, currencies));
            System.out.printf("Najdroższy produkt: %s - %s EUR%n",
                    mostValuableProduct.getName(),
                    calculatePriceInEuro(mostValuableProduct.getPrice(), rateForMostValuableProduct));
            System.out.printf("Najtańszy produkt: %s - %s EUR%n",
                    leastValuableProduct.getName(),
                    calculatePriceInEuro(leastValuableProduct.getPrice(), rateForLeastValuableProduct));
        } catch (UncheckedIOException ex) {
            System.out.println("Wystąpił problem podczas czytania pliku");
        }
    }

    public static BigDecimal calculatePriceInEuro(BigDecimal price, BigDecimal rate) {
        return price.divide(rate, ROUNDING_SCALE, RoundingMode.UP);
    }

}
