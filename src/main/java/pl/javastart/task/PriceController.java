package pl.javastart.task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PriceController {
    private static final int ROUNDING_SCALE = 10;
    private static final int ROUNDING_MODE = 0;

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
            System.err.println("Wystąpił problem podczas czytania pliku");
            return List.of();
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
            System.err.println("Wystąpił problem podczas czytania pliku");
            return List.of();
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
            BigDecimal euroPrice = product.getPrice()
                    .divide(rate, ROUNDING_SCALE, ROUNDING_MODE);
            sum = sum.add(euroPrice);
        }
        return sum;
    }

    public static BigDecimal getAveragePriceInEuro(List<Product> products, List<Currency> currencies) {
        BigDecimal sum = getSumInEuro(products, currencies);
        return sum.divide(new BigDecimal(products.size()), ROUNDING_SCALE, ROUNDING_MODE);
    }

    public static Product getMostValuableProduct(List<Product> products) {
        if (products.size() == 0) {
            throw new EmptyFileException("Brak produktów");
        }
        Product mostValuable = products.get(0);
        for (Product product : products) {
            if (product.getPrice().compareTo(mostValuable.getPrice()) > 0) {
                mostValuable = product;
            }
        }
        return mostValuable;
    }

    public static Product getLeastValuableProduct(List<Product> products) {
        if (products.size() == 0) {
            throw new EmptyFileException("Brak produktów");
        }
        Product leastValuable = products.get(0);
        for (Product product : products) {
            if (product.getPrice().compareTo(leastValuable.getPrice()) < 0) {
                leastValuable = product;
            }
        }
        return leastValuable;
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
        List<Product> products = readProducts(productsFileName);
        List<Currency> currencies = readCurrencies(currenciesFileName);
        Product mostValuableProduct = getMostValuableProduct(products);
        BigDecimal rateForMostValuableProduct = findRate(mostValuableProduct.getCurrency(), currencies);
        Product leastValuableProduct = getLeastValuableProduct(products);
        BigDecimal rateForLeastValuableProduct = findRate(leastValuableProduct.getCurrency(), currencies);

        System.out.printf("Suma wszystkich produktów: %s EUR%n", getSumInEuro(products, currencies));
        System.out.printf("Średnia wartość produktu: %s EUR%n",
                getAveragePriceInEuro(products, currencies));
        System.out.printf("Najdroższy produkt: %s - %s EUR%n",
                mostValuableProduct.getName(),
                calculatePriceInEuro(mostValuableProduct.getPrice(), rateForMostValuableProduct));
        System.out.printf("Najtańszy produkt: %s - %s EUR%n",
                leastValuableProduct.getName(),
                calculatePriceInEuro(leastValuableProduct.getPrice(), rateForLeastValuableProduct));
    }

    public static BigDecimal calculatePriceInEuro(BigDecimal price, BigDecimal rate) {
        return price.divide(rate, ROUNDING_SCALE, ROUNDING_MODE);
    }

}
