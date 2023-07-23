package pl.javastart.task;

public class Main {

    public static void main(String[] args) {
        PriceController.showStatistics("src/main/resources/products.csv",
                "src/main/resources/currencies.csv");
    }
}
