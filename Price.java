import java.util.ArrayList;

public class Price {
    private final String date;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final long volume;

    public Price(String date, double open, double high, double low, double close, long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }

    public double getChange() {
        return close - open;
    }

    public double getPercentChange() {
        return open == 0 ? 0 : getChange() / open * 100;
    }

    public double getRange() {
        return high - low;
    }

    public static Price getHighestClose(ArrayList<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }

        Price highestClosePrice = prices.get(0);
        for (Price price : prices) {
            if (price.getClose() > highestClosePrice.getClose()) {
                highestClosePrice = price;
            }
        }
        return highestClosePrice;
    }

    public static Price getLowestClose(ArrayList<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }

        Price lowestClosePrice = prices.get(0);
        for (Price price : prices) {
            if (price.getClose() < lowestClosePrice.getClose()) {
                lowestClosePrice = price;
            }
        }
        return lowestClosePrice;
    }

    //getting the best trading day based on the highest percent change
    public static Price getBestTradingDay(ArrayList<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;    
}
    
        Price bestTradingDay = prices.get(0);
        for (Price price : prices) {
            if (price.getPercentChange() > bestTradingDay.getPercentChange()) {
                bestTradingDay = price;
            }
        }
        return bestTradingDay;
    }

    //getting the worst trading day based on the lowest percent change
    public static Price getWorstTradingDay(ArrayList<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }

        Price worstTradingDay = prices.get(0);
        for (Price price : prices) {
            if (price.getPercentChange() < worstTradingDay.getPercentChange()) {
                worstTradingDay = price;
            }
        }
        return worstTradingDay;
    }
}   