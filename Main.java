import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String DATASET_FOLDER = "Datasets";
    private static final String FILE_ENDING = "_2025.csv";

    public static String processMarketData(String line) {
        System.out.println(line);
        return line;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Choose an option (1-5): ");
            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                analyzeStock(input);
            } else if (choice.equals("2")) {
                compareStocks(input);
            } else if (choice.equals("3")) {
                showAvailableStocks();
            } else if (choice.equals("4")) {
                showMarketSummary();
            } 
            else if (choice.equals("5")) {
                showMarketHistory(input);
            }else if (choice.equals("6")) {
                running = false;
                System.out.println("Thanks for visiting the Mini Market Terminal!");
            } else {
                System.out.println("Please choose a number from 1 to 5.");
            }
            System.out.println();
        }
        input.close();
    }

    private static void printMenu() {
        System.out.println("==========================================");
        System.out.println("          MINI MARKET TERMINAL");
        System.out.println("==========================================");
        System.out.println("1. Analyze a stock");
        System.out.println("2. Compare two stocks");
        System.out.println("3. Browse available stocks");
        System.out.println("4. View market summary");
        System.out.println("5. View market history");
        System.out.println("6. Exit");
        System.out.println("------------------------------------------");
    }

    private static void analyzeStock(Scanner input) {
        System.out.print("Enter a ticker symbol: ");
        String ticker = input.nextLine().trim().toUpperCase();

        try {
            ArrayList<Price> prices = loadPrices(ticker);
            if (prices.isEmpty()) {
                System.out.println("No price records were found for " + ticker + ".");
                return;
            }

            Price latest = prices.get(prices.size() - 1);
            Price highest = Price.getHighestClose(prices);
            Price lowest = Price.getLowestClose(prices);
            Price bestDay = Price.getBestTradingDay(prices);
            Price worstDay = Price.getWorstTradingDay(prices);

            System.out.println("\n--- " + ticker + " STOCK ANALYSIS ---");
            System.out.println("Period: " + prices.get(0).getDate() + " to " + latest.getDate());
            System.out.printf("Open: $%.2f%n", latest.getOpen());
            System.out.printf("High: $%.2f%n", latest.getHigh());
            System.out.printf("Low: $%.2f%n", latest.getLow());
            System.out.printf("Close: $%.2f%n", latest.getClose());
            System.out.printf("Change: $%.2f (%.2f%%)%n",
                    latest.getChange(), latest.getPercentChange());
            System.out.printf("Trading range: $%.2f%n", latest.getRange());
            System.out.printf("Volume: %,d%n", latest.getVolume());
            System.out.printf("Highest close: $%.2f on %s%n",
                    highest.getClose(), highest.getDate());
            System.out.printf("Lowest close: $%.2f on %s%n",
                    lowest.getClose(), lowest.getDate());
            System.out.printf("Best trading day: %s (%.2f%%)%n",
                    bestDay.getDate(), bestDay.getPercentChange());
            System.out.printf("Worst trading day: %s (%.2f%%)%n",
                    worstDay.getDate(), worstDay.getPercentChange());
        } catch (IOException e) {
            System.out.println("Could not open data for " + ticker + ".");
            System.out.println(e.getMessage());
        }
    }

    private static void compareStocks(Scanner input) {
        System.out.print("Enter the first ticker: ");
        String firstTicker = input.nextLine().trim().toUpperCase();
        System.out.print("Enter the second ticker: ");
        String secondTicker = input.nextLine().trim().toUpperCase();

        if (firstTicker.equals(secondTicker)) {
            System.out.println("Enter two different ticker symbols.");
            return;
        }

        try {
            ArrayList<Price> firstPrices = loadPrices(firstTicker);
            ArrayList<Price> secondPrices = loadPrices(secondTicker);

            if (firstPrices.isEmpty() || secondPrices.isEmpty()) {
                System.out.println("One of the stocks has no price records.");
                return;
            }

            Price first = firstPrices.get(firstPrices.size() - 1);
            Price second = secondPrices.get(secondPrices.size() - 1);

            System.out.println("\n--- STOCK COMPARISON ---");
            System.out.printf("%-10s %-14s %-14s %-14s%n",
                    "TICKER", "DATE", "CLOSE", "CHANGE");
            System.out.printf("%-10s %-14s $%-13.2f $%.2f (%+.2f%%)%n",
                    firstTicker, first.getDate(), first.getClose(),
                    first.getChange(), first.getPercentChange());
            System.out.printf("%-10s %-14s $%-13.2f $%.2f (%+.2f%%)%n",
                    secondTicker, second.getDate(), second.getClose(),
                    second.getChange(), second.getPercentChange());
        } catch (IOException e) {
            System.out.println("Could not load one of those stocks.");
            System.out.println(e.getMessage());
        }
    }

    private static void showAvailableStocks() {
        File folder = new File(DATASET_FOLDER);
        File[] files = folder.listFiles();

        System.out.println("\n--- AVAILABLE STOCKS ---");
        if (files == null || files.length == 0) {
            System.out.println("No stock files found in the Datasets folder.");
            return;
        }

        boolean foundStock = false;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(FILE_ENDING)) {
                String ticker = fileName.substring(0, fileName.length() - FILE_ENDING.length());
                System.out.println(ticker);
                foundStock = true;
            }
        }

        if (!foundStock) {
            System.out.println("No stock files found in the Datasets folder.");
        }
    }

    private static void showMarketSummary() {
        File folder = new File(DATASET_FOLDER);
        File[] files = folder.listFiles();
        if (files == null) {
            System.out.println("Could not find the Datasets folder.");
            return;
        }

        int rising = 0;
        int falling = 0;
        int unchanged = 0;
        int numberOfStocks = 0;
        double totalChange = 0;
        String bestTicker = "";
        String worstTicker = "";
        double bestChange = 0;
        double worstChange = 0;

        System.out.println("\n--- MARKET SUMMARY ---");
        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.endsWith(FILE_ENDING)) {
                continue;
            }

            String ticker = fileName.substring(0, fileName.length() - FILE_ENDING.length());
            try {
                ArrayList<Price> prices = loadPrices(ticker);
                if (prices.isEmpty()) {
                    continue;
                }

                Price latest = prices.get(prices.size() - 1);
                double change = latest.getPercentChange();
                System.out.printf("%s: $%.2f (%+.2f%%)%n",
                        ticker, latest.getClose(), change);

                if (change > 0) {
                    rising++;
                } else if (change < 0) {
                    falling++;
                } else {
                    unchanged++;
                }

                if (numberOfStocks == 0 || change > bestChange) {
                    bestChange = change;
                    bestTicker = ticker;
                }
                if (numberOfStocks == 0 || change < worstChange) {
                    worstChange = change;
                    worstTicker = ticker;
                }
                totalChange += change;
                numberOfStocks++;
            } catch (IOException e) {
                System.out.println("Could not load " + ticker + ": " + e.getMessage());
            }
        }

        if (numberOfStocks == 0) {
            System.out.println("No stock data was available.");
            return;
        }

        System.out.println("-------------------------");
        System.out.println("Rising: " + rising + " | Falling: " + falling
                + " | Unchanged: " + unchanged);
        System.out.printf("Average daily change: %+.2f%%%n", totalChange / numberOfStocks);
        System.out.printf("Best performer: %s (%+.2f%%)%n", bestTicker, bestChange);
        System.out.printf("Worst performer: %s (%+.2f%%)%n", worstTicker, worstChange);
    }

    private static ArrayList<Price> loadPrices(String ticker) throws IOException {
        File file = new File(DATASET_FOLDER, ticker + FILE_ENDING);
        if (!file.exists()) {
            throw new IOException("No CSV file was found. Choose a ticker from the available stocks.");
        }

        ArrayList<Price> prices = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        boolean firstLine = true;
        int lineNumber = 0;

        try {
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] columns = line.split(",");
                if (columns.length < 6) {
                    System.out.println("Skipping incomplete row " + lineNumber + ".");
                    continue;
                }

                try {
                    Price price = new Price(
                            columns[0],
                            Double.parseDouble(columns[1]),
                            Double.parseDouble(columns[2]),
                            Double.parseDouble(columns[3]),
                            Double.parseDouble(columns[4]),
                            Long.parseLong(columns[5])
                    );
                    prices.add(price);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping row " + lineNumber + " because it has invalid numbers.");
                }
            }
        } finally {
            reader.close();
        }

        return prices;
    }

    //market history method for specific stock
    private static void showMarketHistory(Scanner input) {
        System.out.print("Enter a ticker symbol: ");
        String ticker = input.nextLine().trim().toUpperCase(); 
        ArrayList<Price> prices = null;
        try {
            prices = loadPrices(ticker);
        } catch (IOException e) {
            System.out.println("Error loading price data: " + e.getMessage());
            return;
        }
        if (prices.isEmpty()) {
            System.out.println("No price data available for " + ticker);
            return;
        }
        System.out.println("Market history for " + ticker + ":");
        for (Price price : prices) {
            System.out.printf("%s: Open=%.2f, High=%.2f, Low=%.2f, Close=%.2f, Volume=%d%n",
                    price.getDate(), price.getOpen(), price.getHigh(), price.getLow(), price.getClose(), price.getVolume());
        }
    }
}
