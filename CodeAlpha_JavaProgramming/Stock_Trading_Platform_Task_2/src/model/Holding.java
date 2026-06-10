package model;
import java.io.Serializable;
public class Holding implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String symbol;
    private       int    quantity;
    private       double avgCostBasis;     
    private       double totalInvested;    

    public Holding(String symbol, int quantity, double purchasePrice) {
        this.symbol        = symbol;
        this.quantity      = quantity;
        this.avgCostBasis  = purchasePrice;
        this.totalInvested = quantity * purchasePrice;
    }

    public void addShares(int qty, double price) {
        totalInvested  += qty * price;
        quantity       += qty;
        avgCostBasis    = totalInvested / quantity;
    }

    public boolean removeShares(int qty) {
        if (qty > quantity) return false;
        totalInvested  -= avgCostBasis * qty;     // reduce at avg cost
        quantity       -= qty;
        if (quantity == 0) totalInvested = 0;
        return true;
    }

    //  Computed P&L 
    public double getCurrentValue(double currentPrice) { return quantity * currentPrice; }
    public double getCostBasis()                       { return quantity * avgCostBasis;  }
    public double getUnrealisedPL(double currentPrice) {
        return getCurrentValue(currentPrice) - getCostBasis();
    }
    public double getUnrealisedPLPct(double currentPrice) {
        if (getCostBasis() == 0) return 0;
        return (getUnrealisedPL(currentPrice) / getCostBasis()) * 100.0;
    }

    //  Getters 
    public String getSymbol()       { return symbol;       }
    public int    getQuantity()     { return quantity;     }
    public double getAvgCostBasis() { return avgCostBasis; }
    public boolean isEmpty()        { return quantity == 0; }
}
