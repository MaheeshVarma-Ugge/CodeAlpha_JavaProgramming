package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Immutable record of a single BUY or SELL trade.
public class Transaction implements Serializable {

    private static final long             serialVersionUID = 1L;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    private final TransactionType type;
    private final String          symbol;
    private final int             quantity;
    private final double          pricePerShare;
    private final double          totalValue;      
    private final double          brokerageFee;     
    private final double          netAmount;       
    private final String          timestamp;
    private final String          note;

    public Transaction(TransactionType type, String symbol,
                       int quantity, double pricePerShare, String note) {
        this.type          = type;
        this.symbol        = symbol;
        this.quantity      = quantity;
        this.pricePerShare = pricePerShare;
        this.totalValue    = quantity * pricePerShare;
        this.brokerageFee  = Math.round(totalValue * 0.001 * 100.0) / 100.0;
        // BUY: you pay totalValue + fee; SELL: you receive totalValue - fee
        this.netAmount     = (type == TransactionType.BUY)
                             ? totalValue + brokerageFee
                             : totalValue - brokerageFee;
        this.timestamp     = LocalDateTime.now().format(FMT);
        this.note          = note;
    }

    Transaction(TransactionType type, String symbol, int quantity,
                double pricePerShare, double totalValue, double brokerageFee,
                double netAmount, String timestamp, String note) {
        this.type          = type;
        this.symbol        = symbol;
        this.quantity      = quantity;
        this.pricePerShare = pricePerShare;
        this.totalValue    = totalValue;
        this.brokerageFee  = brokerageFee;
        this.netAmount     = netAmount;
        this.timestamp     = timestamp;
        this.note          = note;
    }
    // ── Formatted single-line summary 

    public String toString() {
        String side = type == TransactionType.BUY ? "BUY " : "SELL";
        return String.format(
            "  [%s] %s  %-8s  %4d shares @ Rs%9.2f  " +
            "Total: Rs%10.2f  Fee: Rs%6.2f  Net: Rs%10.2f  | %s",
            timestamp, side, symbol, quantity, pricePerShare,
            totalValue, brokerageFee, netAmount, note);
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public TransactionType getType()          { return type;          }
    public String          getSymbol()        { return symbol;        }
    public int             getQuantity()      { return quantity;      }
    public double          getPricePerShare() { return pricePerShare; }
    public double          getTotalValue()    { return totalValue;    }
    public double          getBrokerageFee()  { return brokerageFee;  }
    public double          getNetAmount()     { return netAmount;     }
    public String          getTimestamp()     { return timestamp;     }
    public String          getNote()          { return note;          }
}
