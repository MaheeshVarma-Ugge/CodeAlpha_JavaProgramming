package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String name;
    private final String email;
    private       double cash;
    private final double startingCapital;
    private       double realisedPL;       

    private final Map<String, Holding>    portfolio    = new LinkedHashMap<>();
    private final List<Transaction>       transactions = new ArrayList<>();

    // ── Constructor 
    public User(String userId, String name, String email, double startingCapital) {
        this.userId          = userId;
        this.name            = name;
        this.email           = email;
        this.cash            = startingCapital;
        this.startingCapital = startingCapital;
        this.realisedPL      = 0.0;
    }

    // ── Trading operations 

    public String buy(Stock stock, int qty) {
        if (qty <= 0) return "Quantity must be a positive integer.";
        Transaction tx = new Transaction(
            TransactionType.BUY, stock.getSymbol(), qty,
            stock.getCurrentPrice(), "Market BUY");
        if (tx.getNetAmount() > cash)
            return String.format(
                "Insufficient funds. Need Rs %.2f, available Rs %.2f.",
                tx.getNetAmount(), cash);

        cash -= tx.getNetAmount();
        portfolio.computeIfAbsent(
            stock.getSymbol(),
            s -> new Holding(s, 0, stock.getCurrentPrice())
        ).addShares(qty, stock.getCurrentPrice());
        transactions.add(tx);
        return null;   // success
    }

    public String sell(Stock stock, int qty) {
        if (qty <= 0) return "Quantity must be a positive integer.";
        Holding h = portfolio.get(stock.getSymbol());
        if (h == null || h.getQuantity() < qty)
            return String.format(
                "Not enough shares. You hold %d share(s) of %s.",
                h == null ? 0 : h.getQuantity(), stock.getSymbol());

        Transaction tx = new Transaction(
            TransactionType.SELL, stock.getSymbol(), qty,
            stock.getCurrentPrice(), "Market SELL");

        // Realise P&L: (sell price - avg cost) * qty - fees
        double costBasisForLot = h.getAvgCostBasis() * qty;
        realisedPL += (tx.getNetAmount() - costBasisForLot);

        cash += tx.getNetAmount();
        h.removeShares(qty);
        if (h.isEmpty()) portfolio.remove(stock.getSymbol());
        transactions.add(tx);
        return null;
    }

    // ── Portfolio metrics 
    public double getPortfolioValue(Map<String, Stock> market) {
        double total = 0;
        for (Holding h : portfolio.values()) {
            Stock s = market.get(h.getSymbol());
            if (s != null) total += h.getCurrentValue(s.getCurrentPrice());
        }
        return total;
    }

    public double getNetWorth(Map<String, Stock> market) {
        return cash + getPortfolioValue(market);
    }

    public double getOverallPLPct(Map<String, Stock> market) {
        double nw = getNetWorth(market);
        return ((nw - startingCapital) / startingCapital) * 100.0;
    }

    // ── Getters
    public String                  getUserId()          { return userId;          }
    public String                  getName()            { return name;            }
    public String                  getEmail()           { return email;           }
    public double                  getCash()            { return cash;            }
    public double                  getStartingCapital() { return startingCapital; }
    public double                  getRealisedPL()      { return realisedPL;      }
    public Map<String, Holding>    getPortfolio()       { return Collections.unmodifiableMap(portfolio); }
    public List<Transaction>       getTransactions()    { return Collections.unmodifiableList(transactions); }

    // ── Load helpers (used by PersistenceService only)
    public void setCashDirect(double cash)                          { this.cash = cash; }
    public void setRealisedPLDirect(double realisedPL)              { this.realisedPL = realisedPL; }
    public void addHoldingDirect(String symbol, int qty, double avg) {
        Holding h = new Holding(symbol, 0, avg);
        h.addShares(qty, avg);
        portfolio.put(symbol, h);
    }
    public void addTransactionDirect(TransactionType type, String symbol,
            int qty, double pricePerShare, double totalValue,
            double brokerageFee, double netAmount, String timestamp, String note) {
        transactions.add(new Transaction(type, symbol, qty, pricePerShare,
            totalValue, brokerageFee, netAmount, timestamp, note));
    }
}
