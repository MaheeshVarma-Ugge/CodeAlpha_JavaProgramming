package service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.*;

public class PersistenceService {

    private static final String DATA_DIR  = "data";
    private static final String EXT       = ".txt";
    private static final String CSV_EXT   = "_trades.csv";
    private static final DateTimeFormatter STAMP =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    static {
        try { Files.createDirectories(Paths.get(DATA_DIR)); }
        catch (IOException ignored) {}
    }

    public static String saveUser(User user) {
        String path = DATA_DIR + File.separator + user.getUserId() + EXT;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("userId=" + user.getUserId());
            pw.println("name=" + user.getName());
            pw.println("email=" + user.getEmail());
            pw.println("cash=" + user.getCash());
            pw.println("startingCapital=" + user.getStartingCapital());
            pw.println("realisedPL=" + user.getRealisedPL());
            pw.println("[holdings]");
            for (Holding h : user.getPortfolio().values()) {
                pw.println(h.getSymbol() + "," + h.getQuantity() + "," + h.getAvgCostBasis());
            }
            pw.println("[transactions]");
            for (Transaction tx : user.getTransactions()) {
                pw.println(tx.getType() + "," + tx.getSymbol() + "," + tx.getQuantity() + ","
                    + tx.getPricePerShare() + "," + tx.getTotalValue() + ","
                    + tx.getBrokerageFee() + "," + tx.getNetAmount() + ","
                    + tx.getTimestamp() + "," + tx.getNote());
            }
            return path;
        } catch (IOException e) {
            System.err.println("  [SAVE ERROR] " + e.getMessage());
            return null;
        }
    }

    public static User loadUser(String userId) {
        String path = DATA_DIR + File.separator + userId + EXT;
        if (!Files.exists(Paths.get(path))) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            String id = "", name = "", email = "";
            double cash = 0, startingCapital = 0, realisedPL = 0;
            java.util.List<String[]> holdingLines = new java.util.ArrayList<>();
            java.util.List<String[]> txLines = new java.util.ArrayList<>();
            String section = "";

            while ((line = br.readLine()) != null) {
                if (line.startsWith("userId="))          id             = line.substring(7);
                else if (line.startsWith("name="))       name           = line.substring(5);
                else if (line.startsWith("email="))      email          = line.substring(6);
                else if (line.startsWith("cash="))       cash           = Double.parseDouble(line.substring(5));
                else if (line.startsWith("startingCapital=")) startingCapital = Double.parseDouble(line.substring(16));
                else if (line.startsWith("realisedPL=")) realisedPL     = Double.parseDouble(line.substring(11));
                else if (line.equals("[holdings]"))      section = "holdings";
                else if (line.equals("[transactions]"))  section = "transactions";
                else if (!line.isEmpty()) {
                    if (section.equals("holdings"))      holdingLines.add(line.split(",", 3));
                    else if (section.equals("transactions")) txLines.add(line.split(",", 9));
                }
            }

            User user = new User(id, name, email, startingCapital);
            user.setCashDirect(cash);
            user.setRealisedPLDirect(realisedPL);
            for (String[] parts : holdingLines) {
                if (parts.length == 3) {
                    user.addHoldingDirect(parts[0], Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
                }
            }
            for (String[] parts : txLines) {
                if (parts.length == 9) {
                    user.addTransactionDirect(
                        TransactionType.valueOf(parts[0]), parts[1],
                        Integer.parseInt(parts[2]), Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]), Double.parseDouble(parts[5]),
                        Double.parseDouble(parts[6]), parts[7], parts[8]);
                }
            }
            return user;
        } catch (IOException e) {
            System.err.println("  [LOAD ERROR] " + e.getMessage());
            return null;
        }
    }

    public static boolean hasSavedData(String userId) {
        return Files.exists(Paths.get(DATA_DIR, userId + EXT));
    }

    public static String exportTradesCSV(User user) {
        String filename = user.getUserId() + "_trades_"
            + LocalDateTime.now().format(STAMP) + ".csv";
        String path = DATA_DIR + File.separator + filename;

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("Timestamp,Type,Symbol,Qty,PricePerShare,TotalValue,BrokerageFee,NetAmount,Note");
            for (var tx : user.getTransactions()) {
                pw.printf("%s,%s,%s,%d,%.2f,%.2f,%.2f,%.2f,%s%n",
                    tx.getTimestamp(),
                    tx.getType(),
                    tx.getSymbol(),
                    tx.getQuantity(),
                    tx.getPricePerShare(),
                    tx.getTotalValue(),
                    tx.getBrokerageFee(),
                    tx.getNetAmount(),
                    tx.getNote());
            }
            return path;
        } catch (IOException e) {
            System.err.println("  [CSV ERROR] " + e.getMessage());
            return null;
        }
    }

    public static boolean deleteUser(String userId) {
        try {
            return Files.deleteIfExists(Paths.get(DATA_DIR, userId + EXT));
        } catch (IOException e) {
            return false;
        }
    }
}
