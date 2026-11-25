public class Main {
    public static String processMarketData(String line) {
        System.out.println(line);
        return line;
    }

    public static void main(String[] args) {
        new MarketTerminal().run();
    }
}
