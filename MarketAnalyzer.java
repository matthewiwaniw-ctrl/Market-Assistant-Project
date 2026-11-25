import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MarketAnalyzer {
    public StockAnalysis analyzeStock(ArrayList<Price> prices) {
        Price latest = getLatestPrice(prices);
        return new StockAnalysis(
                latest,
                Price.getHighestClose(prices),
                Price.getLowestClose(prices),
                Price.getBestTradingDay(prices),
                Price.getWorstTradingDay(prices)
        );
    }

    public Price getLatestPrice(ArrayList<Price> prices) {
        return prices.get(prices.size() - 1);
    }

    public PeriodAnalysis analyzePeriod(ArrayList<Price> prices, String startDate, String endDate) {
        ArrayList<Price> periodPrices = filterByDate(prices, startDate, endDate);
        if (periodPrices.isEmpty()) {
            return null;
        }

        double totalClose = 0;
        double highestPrice = periodPrices.get(0).getHigh();
        double lowestPrice = periodPrices.get(0).getLow();
        long totalVolume = 0;

        for (Price price : periodPrices) {
            totalClose += price.getClose();
            if (price.getHigh() > highestPrice) {
                highestPrice = price.getHigh();
            }
            if (price.getLow() < lowestPrice) {
                lowestPrice = price.getLow();
            }
            totalVolume += price.getVolume();
        }

        Price firstPrice = periodPrices.get(0);
        Price lastPrice = periodPrices.get(periodPrices.size() - 1);
        double periodReturn = firstPrice.getClose() == 0 ? 0
                : (lastPrice.getClose() - firstPrice.getClose()) / firstPrice.getClose() * 100;

        return new PeriodAnalysis(periodReturn, totalClose / periodPrices.size(), highestPrice,
                lowestPrice, (double) totalVolume / periodPrices.size());
    }

    public ArrayList<Price> filterAndSortPrices(ArrayList<Price> prices, String startDate,
            String endDate, int sortOption) {
        ArrayList<Price> filteredPrices = filterByDate(prices, startDate, endDate);
        Comparator<Price> comparator;

        if (sortOption == 2) {
            comparator = Comparator.comparingDouble(Price::getClose);
        } else if (sortOption == 3) {
            comparator = Comparator.comparingLong(Price::getVolume).reversed();
        } else {
            comparator = Comparator.comparing(Price::getDate);
        }

        Collections.sort(filteredPrices, comparator);
        return filteredPrices;
    }

    private ArrayList<Price> filterByDate(ArrayList<Price> prices, String startDate, String endDate) {
        ArrayList<Price> filteredPrices = new ArrayList<>();
        for (Price price : prices) {
            if (price.getDate().compareTo(startDate) >= 0
                    && price.getDate().compareTo(endDate) <= 0) {
                filteredPrices.add(price);
            }
        }
        return filteredPrices;
    }

    public MarketSummary summarizeMarket(Map<String, Price> latestPrices) {
        int rising = 0;
        int falling = 0;
        int unchanged = 0;
        double totalChange = 0;
        String bestTicker = "";
        String worstTicker = "";
        double bestChange = 0;
        double worstChange = 0;
        int numberOfStocks = 0;

        for (Map.Entry<String, Price> entry : latestPrices.entrySet()) {
            double change = entry.getValue().getPercentChange();
            if (change > 0) {
                rising++;
            } else if (change < 0) {
                falling++;
            } else {
                unchanged++;
            }

            if (numberOfStocks == 0 || change > bestChange) {
                bestChange = change;
                bestTicker = entry.getKey();
            }
            if (numberOfStocks == 0 || change < worstChange) {
                worstChange = change;
                worstTicker = entry.getKey();
            }
            totalChange += change;
            numberOfStocks++;
        }

        return new MarketSummary(rising, falling, unchanged, totalChange, numberOfStocks,
                bestTicker, worstTicker, bestChange, worstChange);
    }

    public static class StockAnalysis {
        private final Price latest;
        private final Price highestClose;
        private final Price lowestClose;
        private final Price bestDay;
        private final Price worstDay;

        private StockAnalysis(Price latest, Price highestClose, Price lowestClose,
                Price bestDay, Price worstDay) {
            this.latest = latest;
            this.highestClose = highestClose;
            this.lowestClose = lowestClose;
            this.bestDay = bestDay;
            this.worstDay = worstDay;
        }

        public Price getLatest() {
            return latest;
        }

        public Price getHighestClose() {
            return highestClose;
        }

        public Price getLowestClose() {
            return lowestClose;
        }

        public Price getBestDay() {
            return bestDay;
        }

        public Price getWorstDay() {
            return worstDay;
        }
    }

    public static class PeriodAnalysis {
        private final double periodReturn;
        private final double averagePrice;
        private final double highestPrice;
        private final double lowestPrice;
        private final double averageVolume;

        private PeriodAnalysis(double periodReturn, double averagePrice, double highestPrice,
                double lowestPrice, double averageVolume) {
            this.periodReturn = periodReturn;
            this.averagePrice = averagePrice;
            this.highestPrice = highestPrice;
            this.lowestPrice = lowestPrice;
            this.averageVolume = averageVolume;
        }

        public double getPeriodReturn() {
            return periodReturn;
        }

        public double getAveragePrice() {
            return averagePrice;
        }

        public double getHighestPrice() {
            return highestPrice;
        }

        public double getLowestPrice() {
            return lowestPrice;
        }

        public double getAverageVolume() {
            return averageVolume;
        }
    }

    public static class MarketSummary {
        private final int rising;
        private final int falling;
        private final int unchanged;
        private final double totalChange;
        private final int numberOfStocks;
        private final String bestTicker;
        private final String worstTicker;
        private final double bestChange;
        private final double worstChange;

        private MarketSummary(int rising, int falling, int unchanged, double totalChange,
                int numberOfStocks, String bestTicker, String worstTicker,
                double bestChange, double worstChange) {
            this.rising = rising;
            this.falling = falling;
            this.unchanged = unchanged;
            this.totalChange = totalChange;
            this.numberOfStocks = numberOfStocks;
            this.bestTicker = bestTicker;
            this.worstTicker = worstTicker;
            this.bestChange = bestChange;
            this.worstChange = worstChange;
        }

        public int getRising() {
            return rising;
        }

        public int getFalling() {
            return falling;
        }

        public int getUnchanged() {
            return unchanged;
        }

        public double getAverageChange() {
            return totalChange / numberOfStocks;
        }

        public String getBestTicker() {
            return bestTicker;
        }

        public String getWorstTicker() {
            return worstTicker;
        }

        public double getBestChange() {
            return bestChange;
        }

        public double getWorstChange() {
            return worstChange;
        }
    }
}