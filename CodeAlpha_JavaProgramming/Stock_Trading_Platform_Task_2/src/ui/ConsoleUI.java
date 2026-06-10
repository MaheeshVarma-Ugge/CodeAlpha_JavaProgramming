package ui;

import java.util.*;
import model.*;
import service.*;
public class ConsoleUI {

    // ANSI colours
    public static final String GREEN  = "\u001B[32m";
    public static final String RED    = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN   = "\u001B[36m";
    public static final String BOLD   = "\u001B[1m";
    public static final String RESET  = "\u001B[0m";

    private static String c(double v) { return v >= 0 ? GREEN : RED; }
    private static String fmt(double v) {
        return String.format("%s%+.2f%s", c(v), v, RESET);
    }

    // ── Splash ────────────────────────────────────────────────────────────────
    public static void splash() {
        System.out.println();
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║              CodeAlpha Stock Trading Platform                ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    // Market board
    public static void printMarket(Map<String, Stock> stocks) {
        System.out.println();
        System.out.println(BOLD + "  ╔══ LIVE MARKET BOARD ══════════════════════════════════════════════════════════╗" + RESET);
        System.out.printf("  %-10s %-28s %-11s %10s %10s %10s%n",
            "Symbol", "Company", "Sector", "Price", "Change", "Chg%");
        line(75);
        for (Stock s : stocks.values()) {
            String colour = s.getDayChangePct() >= 0 ? GREEN : RED;
            System.out.printf("  " + BOLD + "%-10s" + RESET + " %-28s %-11s %10.2f %s%10.2f%s %s%8.2f%%%s%n",
                s.getSymbol(),
                truncate(s.getCompanyName(), 28),
                s.getSector(),
                s.getCurrentPrice(),
                colour, s.getDayChangeAmt(), RESET,
                colour, s.getDayChangePct(), RESET);
        }
        line(75);
    }

    // ── Stock detail
    public static void printStockDetail(Stock s) {
        String colour = s.getDayChangePct() >= 0 ? GREEN : RED;
        System.out.println();
        System.out.println("  ╔══ STOCK DETAIL ═══════════════════════════════════╗");
        System.out.printf( "  ║  Symbol     : %-35s║%n", s.getSymbol());
        System.out.printf( "  ║  Company    : %-35s║%n", s.getCompanyName());
        System.out.printf( "  ║  Sector     : %-35s║%n", s.getSector());
        System.out.printf( "  ║  Price      : Rs %-32.2f║%n", s.getCurrentPrice());
        System.out.printf( "  ║  Day Open   : Rs %-32.2f║%n", s.getOpenPrice());
        System.out.printf( "  ║  Day High   : Rs %-32.2f║%n", s.getDayHigh());
        System.out.printf( "  ║  Day Low    : Rs %-32.2f║%n", s.getDayLow());
        System.out.printf( "  ║  Day Change : %s%-32s%s║%n",
            colour,
            String.format("Rs %.2f  (%.2f%%)", s.getDayChangeAmt(), s.getDayChangePct()),
            RESET);
        System.out.printf( "  ║  Volume     : %-35s║%n",
            String.format("%,d shares", s.getVolume()));
        System.out.printf( "  ║  Since IPO  : %s%-32s%s║%n",
            colour,
            String.format("%.2f%%", s.getAllTimeChangePct()),
            RESET);
        System.out.printf( "  ║  Trend      : %-35s║%n", s.getSparkline());
        System.out.println("  ╚═══════════════════════════════════════════════════╝");
    }

    // ── Portfolio ─────────────────────────────────────────────────────────────
    public static void printPortfolio(User user, Map<String, Stock> market) {
        System.out.println();
        double portfolioVal = user.getPortfolioValue(market);
        double netWorth     = user.getNetWorth(market);
        double overallPct   = user.getOverallPLPct(market);

        System.out.println(BOLD + "  ╔══ PORTFOLIO ═══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.printf( "  ║  Trader      : %-30s  ID: %-15s║%n", user.getName(), user.getUserId());
        System.out.printf( "  ║  Cash        : Rs %-55.2f║%n", user.getCash());
        System.out.printf( "  ║  Holdings    : Rs %-55.2f║%n", portfolioVal);
        System.out.printf( "  ║  Net Worth   : Rs %-55.2f║%n", netWorth);
        System.out.printf( "  ║  Overall P&L : %s%-57s%s║%n",
            overallPct >= 0 ? GREEN : RED,
            String.format("%.2f%%  (Started: Rs %.2f)", overallPct, user.getStartingCapital()),
            RESET);
        System.out.printf( "  ║  Realised P&L: Rs %-55.2f║%n", user.getRealisedPL());

        if (user.getPortfolio().isEmpty()) {
            System.out.println("  ║  No open positions.                                                       ║");
        } else {
            System.out.println("  ╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.printf( "  ║  %-10s %5s %10s %10s %12s %10s %8s %-5s║%n",
                "Symbol", "Qty", "Avg Cost", "Curr Price", "Curr Value", "Unreal P&L", "P&L %", "");
            line(82);

            double totalUnrealised = 0;
            for (Holding h : user.getPortfolio().values()) {
                Stock s = market.get(h.getSymbol());
                if (s == null) continue;
                double upl    = h.getUnrealisedPL(s.getCurrentPrice());
                double uplPct = h.getUnrealisedPLPct(s.getCurrentPrice());
                totalUnrealised += upl;
                System.out.printf( "  ║  %-10s %5d %10.2f %10.2f %12.2f %s%10.2f%s %s%7.2f%%%s  %s  ║%n",
                    h.getSymbol(), h.getQuantity(),
                    h.getAvgCostBasis(), s.getCurrentPrice(),
                    h.getCurrentValue(s.getCurrentPrice()),
                    upl >= 0 ? GREEN : RED, upl, RESET,
                    uplPct >= 0 ? GREEN : RED, uplPct, RESET,
                    s.getTrendArrow());
            }
            line(82);
            System.out.printf( "  Total Unrealised P&L: %s%n", fmt(totalUnrealised));

            // Sector breakdown
            System.out.println();
            System.out.println("  ── Sector Allocation ──────────────────");
            Map<String, Double> sectors = PortfolioService.sectorBreakdown(user, market);
            sectors.forEach((sector, val) ->
                System.out.printf("  %-14s  Rs %10.2f  (%.1f%%)%n",
                    sector, val, portfolioVal > 0 ? (val / portfolioVal) * 100 : 0));

            // Best / worst
            PortfolioService.bestHolding(user, market).ifPresent(h -> {
                Stock s = market.get(h.getSymbol());
                System.out.printf("%n  🏆 Best  : %s  %s%.2f%s%n",
                    h.getSymbol(), GREEN,
                    h.getUnrealisedPL(s.getCurrentPrice()), RESET);
            });
            PortfolioService.worstHolding(user, market).ifPresent(h -> {
                Stock s = market.get(h.getSymbol());
                System.out.printf("  ⚠  Worst : %s  %s%.2f%s%n",
                    h.getSymbol(), RED,
                    h.getUnrealisedPL(s.getCurrentPrice()), RESET);
            });
        }
        System.out.println();
    }

    // ── Trade history ─────────────────────────────────────────────────────────
    public static void printTradeHistory(User user, int limit) {
        List<Transaction> txs = user.getTransactions();
        System.out.println();
        System.out.println(BOLD + "  ── TRADE HISTORY " +
            (limit > 0 ? "(last " + limit + ")" : "(all)") + " ──" + RESET);
        if (txs.isEmpty()) {
            System.out.println("  No trades recorded yet.");
            return;
        }
        int start = limit > 0 ? Math.max(0, txs.size() - limit) : 0;
        for (int i = txs.size() - 1; i >= start; i--) {
            Transaction tx = txs.get(i);
            String colour  = tx.getType() == TransactionType.BUY ? GREEN : RED;
            System.out.printf("  %s%s%s%n", colour, tx, RESET);
        }
        PortfolioService.printTradeSummary(user);
    }

    // ── Gainers / Losers ──────────────────────────────────────────────────────
    public static void printGainersLosers(MarketService mkt) {
        System.out.println();
        System.out.println(BOLD + "  ── TOP 5 GAINERS ──" + RESET);
        for (Stock s : mkt.getTopGainers(5)) {
            System.out.printf("  %-10s %s%+.2f%%%s%n",
                s.getSymbol(), GREEN, s.getDayChangePct(), RESET);
        }
        System.out.println();
        System.out.println(BOLD + "  ── TOP 5 LOSERS ──" + RESET);
        for (Stock s : mkt.getTopLosers(5)) {
            System.out.printf("  %-10s %s%+.2f%%%s%n",
                s.getSymbol(), RED, s.getDayChangePct(), RESET);
        }
    }

    // ── Search results ────────────────────────────────────────────────────────
    public static void printSearchResults(List<Stock> results) {
        if (results.isEmpty()) {
            System.out.println("  No matching stocks found.");
            return;
        }
        System.out.printf("%n  %-10s %-28s %-12s %10s %8s%n",
            "Symbol", "Company", "Sector", "Price", "Chg%");
        line(72);
        for (Stock s : results) {
            String colour = s.getDayChangePct() >= 0 ? GREEN : RED;
            System.out.printf("  %-10s %-28s %-12s %10.2f %s%+7.2f%%%s%n",
                s.getSymbol(), truncate(s.getCompanyName(), 28),
                s.getSector(), s.getCurrentPrice(),
                colour, s.getDayChangePct(), RESET);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    public static void line(int w) { System.out.println("  " + "─".repeat(w)); }
    public static void line()      { line(80); }

    public static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    public static void success(String msg) {
        System.out.println("  " + GREEN + "✅ " + msg + RESET);
    }
    public static void error(String msg) {
        System.out.println("  " + RED + "❌ " + msg + RESET);
    }
    public static void info(String msg) {
        System.out.println("  " + CYAN + "ℹ  " + msg + RESET);
    }
    public static void warn(String msg) {
        System.out.println("  " + YELLOW + "⚠  " + msg + RESET);
    }
}
