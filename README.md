# Market Assistant Project

The Mini Market Terminal is a Java command-line application that reads historical stock prices from CSV files in `Datasets/`.

## Run

From the project root, compile and launch the application:

```bash
javac Main.java Price.java
java Main
```

The menu offers:

1. Analyze a stock's latest prices, period high/low closes, and best/worst trading days.
2. Compare two stocks' latest available records.
3. Browse the tickers discovered from the dataset files.
4. View a summary of latest daily stock movements.
5. Exit.

Dataset filenames must use the format `TICKER_2025.csv`, with a header row followed by `Date,Open,High,Low,Close,Volume` values. For example, `Datasets/AAPL_2025.csv` is available as ticker `AAPL`.
