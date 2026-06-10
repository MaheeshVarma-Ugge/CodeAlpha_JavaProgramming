package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Stock implements Serializable {

    private static final long   serialVersionUID = 1L;
    private static final Random RNG              = new Random();

    // ── Core fields
    private final String  symbol;
    private final String  companyName;
    private final String  sector;
    private       double  currentPrice;
    private final double  ipoPrice;          
    private       double  openPrice;        
    private       double  dayHigh;
    private       double  dayLow;
    private       long    volume;          
    private final double  volatility;        

    // ── Price history 
    private final List<Double> priceHistory = new ArrayList<>();

    // ── Constructor 
    public Stock(String symbol, String companyName, String sector,
                 double price, double volatility) {
        this.symbol      = symbol;
        this.companyName = companyName;
        this.sector      = sector;
        this.currentPrice = price;
        this.ipoPrice    = price;
        this.openPrice   = price;
        this.dayHigh     = price;
        this.dayLow      = price;
        this.volume      = 0;
        this.volatility  = volatility;
        priceHistory.add(price);
    }

    // ── Price simulation 
    
    public void tick() {
        double change  = currentPrice * (RNG.nextGaussian() * volatility);
        currentPrice   = Math.max(1.0, currentPrice + change);
        currentPrice   = Math.round(currentPrice * 100.0) / 100.0;

        if (currentPrice > dayHigh) dayHigh = currentPrice;
        if (currentPrice < dayLow)  dayLow  = currentPrice;
        volume += (long)(RNG.nextInt(5000) + 1000);

        priceHistory.add(currentPrice);
        if (priceHistory.size() > 50) priceHistory.remove(0);
    }

    // ── Computed metrics 
    public double getDayChangeAmt()  { return currentPrice - openPrice; }
    public double getDayChangePct()  { return (getDayChangeAmt() / openPrice) * 100.0; }
    public double getAllTimeChangePct() { return ((currentPrice - ipoPrice) / ipoPrice) * 100.0; }

    /** 8-character ASCII sparkline of recent price movement */
    public String getSparkline() {
        int take = Math.min(8, priceHistory.size());
        List<Double> window = priceHistory.subList(priceHistory.size() - take, priceHistory.size());
        double min = Collections.min(window);
        double max = Collections.max(window);
        double range = max - min;
        String[] bars = { "▁", "▂", "▃", "▄", "▅", "▆", "▇", "█" };
        StringBuilder sb = new StringBuilder();
        for (double p : window) {
            int idx = (range == 0) ? 3
                    : (int) Math.round(((p - min) / range) * (bars.length - 1));
            sb.append(bars[Math.min(idx, bars.length - 1)]);
        }
        return sb.toString();
    }

    // -Trend arrow based on day-change percent 
    public String getTrendArrow() {
        double pct = getDayChangePct();
        if (pct >  3) return "▲▲";
        if (pct >  0) return "▲ ";
        if (pct < -3) return "▼▼";
        return "▼ ";
    }
    // ANSI colour prefix — green if up, red if down 
    public String getColour() {
        return getDayChangePct() >= 0 ? "\u001B[32m" : "\u001B[31m";
    }
    public static final String RESET = "\u001B[0m";
    public String  getSymbol()        { return symbol;       }
    public String  getCompanyName()   { return companyName;  }
    public String  getSector()        { return sector;       }
    public double  getCurrentPrice()  { return currentPrice; }
    public double  getOpenPrice()     { return openPrice;    }
    public double  getDayHigh()       { return dayHigh;      }
    public double  getDayLow()        { return dayLow;       }
    public long    getVolume()        { return volume;       }
    public double  getIpoPrice()      { return ipoPrice;     }
    public List<Double> getPriceHistory() { return Collections.unmodifiableList(priceHistory); }

    // - Reset open price (call at start of each session) 
    public void resetDay() {
        openPrice = currentPrice;
        dayHigh   = currentPrice;
        dayLow    = currentPrice;
        volume    = 0;
    }
}
