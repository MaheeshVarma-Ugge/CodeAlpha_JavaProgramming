package service;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import model.Stock;

public class MarketService {

    private final Map<String, Stock> stocks = new LinkedHashMap<>();

    // ── Seed with NSE-inspired Indian stocks 
    public MarketService() {
        // symbol, company, sector, basePrice, volatility
        add("RELIANCE", "Reliance Industries Ltd",    "Energy",        2850.50, 0.015);
        add("TCS",      "Tata Consultancy Services",  "IT",            3920.00, 0.012);
        add("INFY",     "Infosys Ltd",                "IT",            1780.25, 0.013);
        add("HDFCBANK", "HDFC Bank Ltd",              "Banking",       1690.00, 0.011);
        add("ICICIBANK","ICICI Bank Ltd",             "Banking",       1120.75, 0.013);
        add("SBILIFE",  "SBI Life Insurance",         "Insurance",     1540.00, 0.016);
        add("WIPRO",    "Wipro Ltd",                  "IT",            570.80, 0.014);
        add("MARUTI",   "Maruti Suzuki India",        "Auto",          12500.00, 0.018);
        add("TATAMOTORS","Tata Motors Ltd",           "Auto",          945.60, 0.022);
        add("ONGC",     "Oil & Natural Gas Corp",     "Energy",         285.40, 0.017);
        add("BHARTI",   "Bharti Airtel Ltd",          "Telecom",       1340.00, 0.014);
        add("SUNPHARMA","Sun Pharmaceutical",         "Pharma",        1650.00, 0.016);
        add("BAJFINANCE","Bajaj Finance Ltd",         "NBFC",          6850.00, 0.020);
        add("HINDUNILVR","Hindustan Unilever",        "FMCG",          2580.00, 0.010);
        add("NIFTY50",  "Nifty 50 Index ETF",        "Index",         22400.00, 0.009);
    }

    private void add(String sym, String name, String sector,
                     double price, double vol) {
        stocks.put(sym, new Stock(sym, name, sector, price, vol));
    }

    public void tickAll() {
        stocks.values().forEach(Stock::tick);
    }

    /** Advance N ticks (used for fast-forward simulation). */
    public void tickAll(int n) {
        for (int i = 0; i < n; i++) tickAll();
    }

    // Reset all stocks to new day (reset open price, day H/L, volume). 
    public void resetDay() {
        stocks.values().forEach(Stock::resetDay);
    }

    // ── Lookup 
    public Stock  getStock(String symbol) { return stocks.get(symbol.toUpperCase()); }
    public boolean exists(String symbol)  { return stocks.containsKey(symbol.toUpperCase()); }

    public Map<String, Stock> getAllStocks() {
        return Collections.unmodifiableMap(stocks);
    }

    // Returns stocks filtered by sector. 
    public Map<String, Stock> getBySector(String sector) {
        Map<String, Stock> result = new LinkedHashMap<>();
        for (Stock s : stocks.values()) {
            if (s.getSector().equalsIgnoreCase(sector)) result.put(s.getSymbol(), s);
        }
        return result;
    }

    // Returns top N gainers sorted by day-change %. 
    public java.util.List<Stock> getTopGainers(int n) {
        java.util.List<Stock> list = new java.util.ArrayList<>(stocks.values());
        list.sort((a, b) -> Double.compare(b.getDayChangePct(), a.getDayChangePct()));
        return list.subList(0, Math.min(n, list.size()));
    }

    // Returns top N losers sorted by day-change %. 
    public java.util.List<Stock> getTopLosers(int n) {
        java.util.List<Stock> list = new java.util.ArrayList<>(stocks.values());
        list.sort((a, b) -> Double.compare(a.getDayChangePct(), b.getDayChangePct()));
        return list.subList(0, Math.min(n, list.size()));
    }
}
