package service;

import java.util.*;
import model.*;

public class PortfolioService {

    public static Map<String, Double> sectorBreakdown(User user, Map<String, Stock> market) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Holding h : user.getPortfolio().values()) {
            Stock s = market.get(h.getSymbol());
            if (s == null) continue;
            result.merge(s.getSector(),
                h.getCurrentValue(s.getCurrentPrice()),
                Double::sum);
        }
        return result;
    }

    // ── Best & worst holdings by unrealised P&L 
    public static Optional<Holding> bestHolding(User user, Map<String, Stock> market) {
        return user.getPortfolio().values().stream()
            .filter(h -> market.containsKey(h.getSymbol()))
            .max(Comparator.comparingDouble(
                h -> h.getUnrealisedPL(market.get(h.getSymbol()).getCurrentPrice())));
    }

    public static Optional<Holding> worstHolding(User user, Map<String, Stock> market) {
        return user.getPortfolio().values().stream()
            .filter(h -> market.containsKey(h.getSymbol()))
            .min(Comparator.comparingDouble(
                h -> h.getUnrealisedPL(market.get(h.getSymbol()).getCurrentPrice())));
    }

    // ── Search stocks by symbol prefix or company name 
    public static List<Stock> search(String query, Map<String, Stock> market) {
        String q = query.toLowerCase();
        List<Stock> results = new ArrayList<>();
        for (Stock s : market.values()) {
            if (s.getSymbol().toLowerCase().contains(q)
                || s.getCompanyName().toLowerCase().contains(q)
                || s.getSector().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    // ── Summary statistics for the trade history 
    public static void printTradeSummary(User user) {
        int buys = 0, sells = 0;
        double totalBought = 0, totalSold = 0, totalFees = 0;
        for (Transaction tx : user.getTransactions()) {
            totalFees += tx.getBrokerageFee();
            if (tx.getType() == TransactionType.BUY) {
                buys++;
                totalBought += tx.getTotalValue();
            } else {
                sells++;
                totalSold += tx.getTotalValue();
            }
        }
        System.out.println();
        System.out.println("  ┌─── Trade Summary ─────────────────────────────────┐");
        System.out.printf( "  │  Total Trades       : %-28d│%n", buys + sells);
        System.out.printf( "  │  Buy  Orders        : %-28d│%n", buys);
        System.out.printf( "  │  Sell Orders        : %-28d│%n", sells);
        System.out.printf( "  │  Total Value Bought : Rs %-25.2f│%n", totalBought);
        System.out.printf( "  │  Total Value Sold   : Rs %-25.2f│%n", totalSold);
        System.out.printf( "  │  Total Brokerage    : Rs %-25.2f│%n", totalFees);
        System.out.printf( "  │  Realised P&L       : Rs %-25.2f│%n", user.getRealisedPL());
        System.out.println("  └────────────────────────────────────────────────────┘");
    }
}
