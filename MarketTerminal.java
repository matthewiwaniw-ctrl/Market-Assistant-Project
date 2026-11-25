import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class MarketTerminal {
    private final MarketDataManager dataManager;
    private final MarketAnalyzer analyzer;
    private final Set<String> watchlist;

    public MarketTerminal() {
        dataManager = new MarketDataManager();
        analyzer = new MarketAnalyzer();
        watchlist = new LinkedHashSet<>();
    }

    public void run() {
        Scanner input = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Choose an option (1-10): ");
            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                analyzeStock(input);
            } else if (choice.equals("2")) {
                compareStocks(input);
            } else if (choice.equals("3")) {
                showAvailableStocks();
            } else if (choice.equals("4")) {
                showMarketSummary();
            } else if (choice.equals("5")) {
                showMarketHistory(input);
            } else if (choice.equals("6")) {
                filterAndSortHistory(input);
            } else if (choice.equals("7")) {
                compareStocksForPeriod(input);
            } else if (choice.equals("8")) {
                showWatchlist();
            } else if (choice.equals("9")) {
                manageWatchlist(input);
            } else if (choice.equals("10")) {
                running = false;
                System.out.println("Thanks for visiting the Mini Market Terminal!");
            } else {
                System.out.println("Please choose a valid menu option.");
            }
            System.out.println();
        }
        input.close();
    }

    private void printMenu() {
        System.out.println("==========================================");
        System.out.println("          MINI MARKET TERMINAL");
        System.out.println("==========================================");
        System.out.println("1. Analyze a stock");
        System.out.println("2. Compare two stocks");
        System.out.println("3. Browse available stocks");
        System.out.println("4. View market summary");
        System.out.println("5. View market history");
        System.out.println("6. Filter and sort stock history");
        System.out.println("7. Compare stocks over a period");
        System.out.println("8. View watchlist");
        System.out.println("9. Manage watchlist");
        System.out.println("10. Exit");
        System.out.println("------------------------------------------");
    }

    private void analyzeStock(Scanner input) {
        System.out.print("Enter a ticker symbol: ");
        String ticker = input.nextLine().trim().toUpperCase();

        try {
            ArrayList<Price> prices = dataManager.loadPrices(ticker);
            if (prices.isEmpty()) {
                System.out.println("No price records were found for " + ticker + ".");
                return;
            }

            MarketAnalyzer.StockAnalysis analysis = analyzer.analyzeStock(prices);
            Price latest = analysis.getLatest();

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
                    analysis.getHighestClose().getClose(), analysis.getHighestClose().getDate());
            System.out.printf("Lowest close: $%.2f on %s%n",
                    analysis.getLowestClose().getClose(), analysis.getLowestClose().getDate());
            System.out.printf("Best trading day: %s (%.2f%%)%n",
                    analysis.getBestDay().getDate(), analysis.getBestDay().getPercentChange());
            System.out.printf("Worst trading day: %s (%.2f%%)%n",
                    analysis.getWorstDay().getDate(), analysis.getWorstDay().getPercentChange());
        } catch (IOException e) {
            System.out.println("Could not open data for " + ticker + ".");
            System.out.println(e.getMessage());
        }
    }

    private void compareStocks(Scanner input) {
        System.out.print("Enter the first ticker: ");
        String firstTicker = input.nextLine().trim().toUpperCase();
        System.out.print("Enter the second ticker: ");
        String secondTicker = input.nextLine().trim().toUpperCase();

        if (firstTicker.equals(secondTicker)) {
            System.out.println("Enter two different ticker symbols.");
            return;
        }

        try {
            ArrayList<Price> firstPrices = dataManager.loadPrices(firstTicker);
            ArrayList<Price> secondPrices = dataManager.loadPrices(secondTicker);

            if (firstPrices.isEmpty() || secondPrices.isEmpty()) {
                System.out.println("One of the stocks has no price records.");
                return;
            }

            Price first = analyzer.getLatestPrice(firstPrices);
            Price second = analyzer.getLatestPrice(secondPrices);

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

    private void compareStocksForPeriod(Scanner input) {
        System.out.print("Enter at least two ticker symbols separated by commas: ");
        String[] tickerEntries = input.nextLine().split(",");
        ArrayList<String> tickers = new ArrayList<>();
        for (String tickerEntry : tickerEntries) {
            String ticker = tickerEntry.trim().toUpperCase();
            if (!ticker.isEmpty() && !tickers.contains(ticker)) {
                tickers.add(ticker);
            }
        }
        if (tickers.size() < 2) {
            System.out.println("Enter at least two different ticker symbols.");
            return;
        }

        System.out.print("Start date (YYYY-MM-DD): ");
        String startDate = input.nextLine().trim();
        System.out.print("End date (YYYY-MM-DD): ");
        String endDate = input.nextLine().trim();
        if (startDate.compareTo(endDate) > 0) {
            System.out.println("The start date must be on or before the end date.");
            return;
        }

        Map<String, MarketAnalyzer.PeriodAnalysis> results = new LinkedHashMap<>();
        for (String ticker : tickers) {
            try {
                ArrayList<Price> prices = dataManager.loadPrices(ticker);
                MarketAnalyzer.PeriodAnalysis result = analyzer.analyzePeriod(prices, startDate, endDate);
                if (result == null) {
                    System.out.println("No price records for " + ticker + " in that period.");
                } else {
                    results.put(ticker, result);
                }
            } catch (IOException e) {
                System.out.println("Could not load " + ticker + ": " + e.getMessage());
            }
        }

        if (results.size() < 2) {
            System.out.println("At least two stocks need data in the selected period.");
            return;
        }

        System.out.println("\n--- PERIOD COMPARISON: " + startDate + " to " + endDate + " ---");
        System.out.printf("%-20s", "METRIC");
        for (String ticker : results.keySet()) {
            System.out.printf("%14s", ticker);
        }
        System.out.println();
        printPeriodMetric(results, "Period Return", 1);
        printPeriodMetric(results, "Average Price", 2);
        printPeriodMetric(results, "Highest Price", 3);
        printPeriodMetric(results, "Lowest Price", 4);
        printPeriodMetric(results, "Average Volume", 5);
    }

    private void printPeriodMetric(Map<String, MarketAnalyzer.PeriodAnalysis> results,
            String label, int metric) {
        System.out.printf("%-20s", label);
        for (MarketAnalyzer.PeriodAnalysis result : results.values()) {
            if (metric == 1) {
                System.out.printf("%13.2f%%", result.getPeriodReturn());
            } else if (metric == 2) {
                System.out.printf("%14.2f", result.getAveragePrice());
            } else if (metric == 3) {
                System.out.printf("%14.2f", result.getHighestPrice());
            } else if (metric == 4) {
                System.out.printf("%14.2f", result.getLowestPrice());
            } else {
                System.out.printf("%14.0f", result.getAverageVolume());
            }
        }
        System.out.println();
    }

    private void showWatchlist() {
        System.out.println("\n--- MY WATCHLIST ---");
        if (watchlist.isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }

        System.out.printf("%-12s %-27s %s%n", "Ticker", "Latest Available Price", "Change");
        System.out.println("-----------------------------------------------------------");
        for (String ticker : watchlist) {
            try {
                ArrayList<Price> prices = dataManager.loadPrices(ticker);
                if (!prices.isEmpty()) {
                    Price latest = analyzer.getLatestPrice(prices);
                    System.out.printf("%-12s $%-26.2f %+.2f%%%n",
                            ticker, latest.getClose(), latest.getPercentChange());
                }
            } catch (IOException e) {
                System.out.println("Could not load " + ticker + ": " + e.getMessage());
            }
        }
    }

    private void manageWatchlist(Scanner input) {
        System.out.println("1. Add a stock");
        System.out.println("2. Remove a stock");
        System.out.print("Choose an option: ");
        String choice = input.nextLine().trim();
        if (!choice.equals("1") && !choice.equals("2")) {
            System.out.println("Please choose 1 or 2.");
            return;
        }

        System.out.print("Enter a ticker symbol: ");
        String ticker = input.nextLine().trim().toUpperCase();
        if (choice.equals("2")) {
            if (watchlist.remove(ticker)) {
                System.out.println(ticker + " removed from your watchlist.");
            } else {
                System.out.println(ticker + " is not in your watchlist.");
            }
            return;
        }

        try {
            ArrayList<Price> prices = dataManager.loadPrices(ticker);
            if (prices.isEmpty()) {
                System.out.println("No price records were found for " + ticker + ".");
            } else if (watchlist.add(ticker)) {
                System.out.println(ticker + " added to your watchlist.");
            } else {
                System.out.println(ticker + " is already in your watchlist.");
            }
        } catch (IOException e) {
            System.out.println("Could not add " + ticker + ": " + e.getMessage());
        }
    }

    private void filterAndSortHistory(Scanner input) {
        System.out.print("Enter a ticker symbol: ");
        String ticker = input.nextLine().trim().toUpperCase();
        System.out.print("Start date (YYYY-MM-DD): ");
        String startDate = input.nextLine().trim();
        System.out.print("End date (YYYY-MM-DD): ");
        String endDate = input.nextLine().trim();
        if (startDate.compareTo(endDate) > 0) {
            System.out.println("The start date must be on or before the end date.");
            return;
        }

        System.out.println("Sort by:");
        System.out.println("1. Date (oldest first)");
        System.out.println("2. Closing price (lowest first)");
        System.out.println("3. Volume (highest first)");
        System.out.print("Choose a sort option: ");
        String sortChoice = input.nextLine().trim();
        int sortOption;
        if (sortChoice.equals("1")) {
            sortOption = 1;
        } else if (sortChoice.equals("2")) {
            sortOption = 2;
        } else if (sortChoice.equals("3")) {
            sortOption = 3;
        } else {
            System.out.println("Please choose 1, 2, or 3.");
            return;
        }

        try {
            ArrayList<Price> prices = dataManager.loadPrices(ticker);
            ArrayList<Price> selectedPrices = analyzer.filterAndSortPrices(
                    prices, startDate, endDate, sortOption);
            if (selectedPrices.isEmpty()) {
                System.out.println("No price records found in that date range.");
                return;
            }

            System.out.println("Market data for " + ticker + " from " + startDate + " to " + endDate + ":");
            for (Price price : selectedPrices) {
                System.out.printf("%s: Close=$%.2f, Volume=%d%n",
                        price.getDate(), price.getClose(), price.getVolume());
            }
        } catch (IOException e) {
            System.out.println("Could not load data for " + ticker + ": " + e.getMessage());
        }
    }

    private void showAvailableStocks() {
        File[] files = dataManager.getDatasetFiles();

        System.out.println("\n--- AVAILABLE STOCKS ---");
        if (files == null || files.length == 0) {
            System.out.println("No stock files found in the Datasets folder.");
            return;
        }

        boolean foundStock = false;
        for (File file : files) {
            if (dataManager.isStockFile(file)) {
                System.out.println(dataManager.getTicker(file));
                foundStock = true;
            }
        }

        if (!foundStock) {
            System.out.println("No stock files found in the Datasets folder.");
        }
    }

    private void showMarketSummary() {
        File[] files = dataManager.getDatasetFiles();
        if (files == null) {
            System.out.println("Could not find the Datasets folder.");
            return;
        }

        System.out.println("\n--- MARKET SUMMARY ---");
        Map<String, Price> latestPrices = new LinkedHashMap<>();
        for (File file : files) {
            if (!dataManager.isStockFile(file)) {
                continue;
            }

            String ticker = dataManager.getTicker(file);
            try {
                ArrayList<Price> prices = dataManager.loadPrices(ticker);
                if (prices.isEmpty()) {
                    continue;
                }

                Price latest = analyzer.getLatestPrice(prices);
                System.out.printf("%s: $%.2f (%+.2f%%)%n",
                        ticker, latest.getClose(), latest.getPercentChange());
                latestPrices.put(ticker, latest);
            } catch (IOException e) {
                System.out.println("Could not load " + ticker + ": " + e.getMessage());
            }
        }

        if (latestPrices.isEmpty()) {
            System.out.println("No stock data was available.");
            return;
        }

        MarketAnalyzer.MarketSummary summary = analyzer.summarizeMarket(latestPrices);
        System.out.println("-------------------------");
        System.out.println("Rising: " + summary.getRising() + " | Falling: " + summary.getFalling()
                + " | Unchanged: " + summary.getUnchanged());
        System.out.printf("Average daily change: %+.2f%%%n", summary.getAverageChange());
        System.out.printf("Best performer: %s (%+.2f%%)%n",
                summary.getBestTicker(), summary.getBestChange());
        System.out.printf("Worst performer: %s (%+.2f%%)%n",
                summary.getWorstTicker(), summary.getWorstChange());
    }

    private void showMarketHistory(Scanner input) {
        System.out.print("Enter a ticker symbol: ");
        String ticker = input.nextLine().trim().toUpperCase();
        ArrayList<Price> prices;
        try {
            prices = dataManager.loadPrices(ticker);
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