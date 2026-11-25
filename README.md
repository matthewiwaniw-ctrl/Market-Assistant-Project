# Market Assistant Project

The Mini Market Terminal is a Java command-line application that reads historical stock prices from CSV files in `Datasets/`.

For this project, all group members were responsible for various methods in the Price class. In the main class, Matt and Aidan worked to extract data from the Market data_2025.zip file. As for the rest of the code in Main, each member worked to create a fully functional terminal for users. On top of this, Aidan was responsible for implementing most of the code that catches invalid inputs and returning error messages.

## Run

From the project root, compile and launch the application:

```bash
javac Main.java MarketTerminal.java MarketDataManager.java MarketAnalyzer.java Price.java
java Main
```

The menu offers:

1. Analyze a stock's latest prices, period high/low closes, and best/worst trading days.
2. Compare two stocks' latest available records.
3. Browse the tickers discovered from the dataset files.
4. View a summary of latest daily stock movements.
5. View a stock's market history.
6. Exit.
7. Compare at least two stocks over a selected date range. The comparison includes period return, average closing price, highest high, lowest low, and average volume.
8. View the watchlist with each ticker's latest available local price and daily change.
9. Add a stock to or remove a stock from the watchlist.
10. Filter a stock's history by date range and sort by date, closing price, or volume.

The watchlist is kept for the current application run. Date inputs use `YYYY-MM-DD` and include both the start and end dates.

Dataset filenames must use the format `TICKER_2025.csv`, with a header row followed by `Date,Open,High,Low,Close,Volume` values. For example, `Datasets/AAPL_2025.csv` is available as ticker `AAPL`.
