//Importing all the required and necessary libraries
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;


public class Main {

    //initializing the variables
    private static String date;
    private static String open;
    private static String high;
    private static String low;
    private static String close;
    private static String volume;
    private static String adjClose;
    private static String ticker;
    private static String marketCap;
    
//process ing the data files
    public static String processMarketData(String line) {
    //Printing the data files to the console
    System.out.println(line);
    return line;
}

    // reading the data files from Dataset folder 
    public static void main(String[] args) {

        //scanner to read the user input
        Scanner input = new Scanner(System.in);
        //Asking the user for a ticker symbol
        System.out.println("Enter a ticker symbol: ");
        //fixing the user input to be normalised into upper cases
        ticker=input.nextLine().trim().toUpperCase();
        

        //getting the name of the data files from the Dataset folder
        String fileName = "Datasets/" + ticker + "_2025.csv";

        //A space (ArrayList) to store the price records
        ArrayList<Price> priceRecords = new ArrayList<>();

        //Using the BufferedReader to read the data files
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean header = true;
            //Reading the data files line by line
            while ((line = reader.readLine()) != null) {
                //Calling the process Method to process the data files
                if (header) {
                    header = false; // Skip the header line
                    continue;
                }
                String[] columns = line.split(",");
                    if (columns.length < 6) {
                        continue;
                    }

                    Price price = new Price(
                        columns[0],
                        Double.parseDouble(columns[1]),
                        Double.parseDouble(columns[2]),
                        Double.parseDouble(columns[3]),
                        Double.parseDouble(columns[4]),
                        Long.parseLong(columns[5])
                    );

                    priceRecords.add(price);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

            if (priceRecords.isEmpty()) {
                System.out.println("No price records found.");
                return;
            }
            //Getting the latest price record from the ArrayList
            Price latest = priceRecords.get(priceRecords.size() - 1);

            System.out.println("Latest date: " + latest.getDate());
            System.out.printf("Close: $%.2f%n", latest.getClose());
            System.out.printf("Change: $%.2f (%.2f%%)%n",
                    latest.getChange(), latest.getPercentChange());
            System.out.printf("Trading range: $%.2f%n", latest.getRange());
                
        
    }
}