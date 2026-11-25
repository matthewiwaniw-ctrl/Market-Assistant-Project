import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MarketDataManager {
    private static final String DATASET_FOLDER = "Datasets";
    private static final String FILE_ENDING = "_2025.csv";

    public File[] getDatasetFiles() {
        return new File(DATASET_FOLDER).listFiles();
    }

    public boolean isStockFile(File file) {
        return file.getName().endsWith(FILE_ENDING);
    }

    public String getTicker(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.length() - FILE_ENDING.length());
    }

    public ArrayList<Price> loadPrices(String ticker) throws IOException {
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
}