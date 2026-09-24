//Importing all the required and necessary libraries
import java.io.*;



public class Main {
    
//process ing the data files
    public static String processMarketData(String line) {
    //Printing the data files to the console
    System.out.println(line);
    return line;
}

    // reading the data files from Dataset folder 
    public static void main(String[] args) {

        //getting the name of the data files from the Dataset folder
        String fileName = "Datasets/AAPL_2025.csv";

        //Using the BufferedReader to read the data files
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            //Reading the data files line by line
            while ((line = reader.readLine()) != null) {
                //Calling the process Method to process the data files
                processMarketData(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        
    }
}