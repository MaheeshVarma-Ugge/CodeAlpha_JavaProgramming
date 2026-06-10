import model.*;
import service.*;
import ui.ConsoleUI;
import util.InputUtil;

public class StockTradingPlatform {

    static MarketService mkt;
    static User          user;

    // ── Entry point 
    public static void main(String[] args) {
        ConsoleUI.splash();
        mkt = new MarketService();
        setupUser();
        mainLoop();
    }

    // ── User setup: new or load saved ─────────────────────────────────────────
    static void setupUser() {
        System.out.println("  ┌─────────────────── Login / Register ───────────────────┐");
        System.out.println("  │  Enter an existing User ID to load your saved profile  │");
        System.out.println("  │        or a new ID to create a fresh account.          │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        String id = InputUtil.readLine("  User ID: ").toUpperCase();
        if (id.isEmpty()) id = "TRADER1";

        if (PersistenceService.hasSavedData(id)) {
            User loaded = PersistenceService.loadUser(id);
            if (loaded != null) {
                user = loaded;
                ConsoleUI.success("Profile loaded for " + user.getName() + "  (ID: " + id + ")");
                ConsoleUI.info(String.format(
                    "Net worth: Rs %.2f  |  Cash: Rs %.2f",
                    user.getNetWorth(mkt.getAllStocks()), user.getCash()));
                return;
            }
        }

        // New account
        String name  = InputUtil.readLine("  Full Name : ");
        String email = InputUtil.readLine("  E-mail    : ");
        System.out.println("  Starting capital:");
        System.out.println("    1. Rs  50,000");
        System.out.println("    2. Rs 1,00,000");
        System.out.println("    3. Rs 5,00,000");
        System.out.println("    4. Custom");
        double capital;
        switch (InputUtil.readChoice("  Choice [1-4]: ")) {
            case "1": capital = 50_000;  break;
            case "2": capital = 100_000; break;
            case "3": capital = 500_000; break;
            default:  capital = InputUtil.readPositiveDouble("  Amount: Rs ");
        }
        user = new User(id,
            name.isEmpty()  ? "Trader" : name,
            email.isEmpty() ? ""       : email,
            capital);
        ConsoleUI.success("Account created — Capital: Rs " + String.format("%.2f", capital));
    }

    // ── Main menu loop ────────────────────────────────────────────────────────
    static void mainLoop() {
        boolean running = true;
        while (running) {
            mkt.tickAll();   // prices move on each menu visit
            System.out.println();
            System.out.println(ConsoleUI.BOLD +
                "  ┌────────────────────────Stock Detail List────────────────────┐"
                + ConsoleUI.RESET);
            System.out.printf("  │  👤 %-15s  Cash: Rs %10.2f  Net Worth: Rs %10.2f │%n",
                user.getName(), user.getCash(),
                user.getNetWorth(mkt.getAllStocks()));
            System.out.println("  ┌─────────────────────────────────────────────────────────────┐");
            System.out.println("  │  1.   Live Market Board                                     │");
            System.out.println("  │  2.   Search / Stock Detail                                 │");
            System.out.println("  │  3.   Buy Stock                                             │");
            System.out.println("  │  4.   Sell Stock                                            │");
            System.out.println("  │  5.   My Portfolio & P&L                                    │");
            System.out.println("  │  6.   Trade History                                         │");
            System.out.println("  │  7.   Top Gainers & Losers                                  │");
            System.out.println("  │  8.   Fast-Forward Market (simulate N ticks)                │");
            System.out.println("  │  9.   Save Portfolio                                        │");
            System.out.println("  │  10.  Export Trades to CSV                                  │");
            System.out.println("  │  11.  Save & Exit                                           │");
            System.out.println("  └─────────────────────────────────────────────────────────────┘");

            switch (InputUtil.readChoice("  Choose: ")) {
                case "1":  ConsoleUI.printMarket(mkt.getAllStocks()); break;
                case "2":  menuSearch();    break;
                case "3":  menuBuy();       break;
                case "4":  menuSell();      break;
                case "5":  ConsoleUI.printPortfolio(user, mkt.getAllStocks()); break;
                case "6":  menuHistory();   break;
                case "7":  ConsoleUI.printGainersLosers(mkt); break;
                case "8":  menuFastForward(); break;
                case "9":  menuSave();      break;
                case "10": menuExportCSV(); break;
                case "11":
                    menuSave();
                    ConsoleUI.printPortfolio(user, mkt.getAllStocks());
                    ConsoleUI.success("Session ended. Happy investing! ");
                    running = false;
                    break;
                default:
                    ConsoleUI.warn("Invalid choice. Enter 1–11.");
            }
        }
    }

    // ── Sub-menus ─────────────────────────────────────────────────────────────

    static void menuSearch() {
        System.out.println("\n  ── SEARCH / DETAIL ──");
        System.out.println("  1. Search by symbol / name / sector");
        System.out.println("  2. Full detail for a symbol");
        if (InputUtil.readChoice("  Choice [1/2]: ").equals("1")) {
            String q = InputUtil.readLine("  Query: ");
            ConsoleUI.printSearchResults(PortfolioService.search(q, mkt.getAllStocks()));
        } else {
            String sym = InputUtil.readLine("  Symbol: ").toUpperCase();
            Stock s = mkt.getStock(sym);
            if (s == null) ConsoleUI.error("Symbol '" + sym + "' not found.");
            else ConsoleUI.printStockDetail(s);
        }
    }

    static void menuBuy() {
        ConsoleUI.printMarket(mkt.getAllStocks());
        String sym = InputUtil.readLine("  Symbol to BUY (Enter to cancel): ").toUpperCase();
        if (sym.isEmpty()) return;
        Stock s = mkt.getStock(sym);
        if (s == null) { ConsoleUI.error("Symbol not found."); return; }

        ConsoleUI.printStockDetail(s);
        int maxAffordable = (int)(user.getCash() / (s.getCurrentPrice() * 1.001));
        ConsoleUI.info(String.format("Cash: Rs %.2f  |  Max affordable: ~%d shares",
            user.getCash(), maxAffordable));

        int qty = InputUtil.readPositiveInt("  Quantity: ");
        System.out.printf("  Est. cost (incl. 0.1%% brokerage): Rs %.2f%n",
            s.getCurrentPrice() * qty * 1.001);

        if (!InputUtil.readYesNo("  Confirm BUY? (y/n): ")) {
            ConsoleUI.info("Order cancelled."); return;
        }
        mkt.tickAll();
        String err = user.buy(s, qty);
        if (err != null) ConsoleUI.error(err);
        else {
            ConsoleUI.success(String.format("Bought %d × %s @ Rs %.2f", qty, sym, s.getCurrentPrice()));
            ConsoleUI.info("Cash remaining: Rs " + String.format("%.2f", user.getCash()));
        }
    }

    static void menuSell() {
        if (user.getPortfolio().isEmpty()) { ConsoleUI.warn("No open positions."); return; }
        ConsoleUI.printPortfolio(user, mkt.getAllStocks());
        String sym = InputUtil.readLine("  Symbol to SELL (Enter to cancel): ").toUpperCase();
        if (sym.isEmpty()) return;
        Stock s = mkt.getStock(sym);
        if (s == null) { ConsoleUI.error("Symbol not found."); return; }

        Holding h = user.getPortfolio().get(sym);
        if (h == null) { ConsoleUI.error("You hold no shares of " + sym + "."); return; }

        System.out.printf("  Holding: %d shares | Avg cost: Rs %.2f | Now: Rs %.2f%n",
            h.getQuantity(), h.getAvgCostBasis(), s.getCurrentPrice());
        System.out.printf("  Unrealised P&L: Rs %.2f (%.2f%%)%n",
            h.getUnrealisedPL(s.getCurrentPrice()),
            h.getUnrealisedPLPct(s.getCurrentPrice()));

        int qty = InputUtil.readPositiveInt("  Quantity to sell: ");
        System.out.printf("  Est. proceeds (after 0.1%% brokerage): Rs %.2f%n",
            s.getCurrentPrice() * qty * 0.999);

        if (!InputUtil.readYesNo("  Confirm SELL? (y/n): ")) {
            ConsoleUI.info("Order cancelled."); return;
        }
        mkt.tickAll();
        String err = user.sell(s, qty);
        if (err != null) ConsoleUI.error(err);
        else {
            ConsoleUI.success(String.format("Sold %d × %s @ Rs %.2f", qty, sym, s.getCurrentPrice()));
            ConsoleUI.info("Cash: Rs " + String.format("%.2f", user.getCash()));
        }
    }

    static void menuHistory() {
        System.out.println("  1. Last 10 trades   2. All trades");
        int limit = InputUtil.readChoice("  Choice: ").equals("1") ? 10 : 0;
        ConsoleUI.printTradeHistory(user, limit);
    }

    static void menuFastForward() {
        int n = InputUtil.readPositiveInt("  Simulate how many ticks? ");
        mkt.tickAll(n);
        ConsoleUI.success("Market advanced by " + n + " tick(s).");
        ConsoleUI.printMarket(mkt.getAllStocks());
    }

    static void menuSave() {
        String path = PersistenceService.saveUser(user);
        if (path != null) ConsoleUI.success("Saved → " + path);
        else ConsoleUI.error("Save failed.");
    }

    static void menuExportCSV() {
        if (user.getTransactions().isEmpty()) { ConsoleUI.warn("No trades to export."); return; }
        String path = PersistenceService.exportTradesCSV(user);
        if (path != null) ConsoleUI.success("CSV exported → " + path);
        else ConsoleUI.error("Export failed.");
    }
}
