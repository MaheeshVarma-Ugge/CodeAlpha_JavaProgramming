# CodeAlpha_StockTradingPlatform

## Project Structure
```
CodeAlpha_StockTradingPlatform/
├── src/
│   ├── StockTradingPlatform.java   (main entry point)
│   ├── model/
│   │   ├── Stock.java
│   │   ├── Holding.java
│   │   ├── Transaction.java
│   │   ├── TransactionType.java
│   │   └── User.java
│   ├── service/
│   │   ├── MarketService.java
│   │   ├── PersistenceService.java
│   │   └── PortfolioService.java
│   ├── ui/
│   │   └── ConsoleUI.java
│   └── util/
│       └── InputUtil.java
├── bin/           (compiled .class files)
├── data/          (saved profiles + CSV exports)
└── README.md
```

## Compile & Run
```bash
#.java files contain the source code written by you.
# Java cannot run .java files directly.
# So Java first converts source code into bytecode.
# That bytecode is stored in .class files,which can be executed by the Java Virtual Machine.
javac -d bin src/model/*.java src/service/*.java src/ui/*.java src/util/*.java src/StockTradingPlatform.java

# Run 
java -cp bin StockTradingPlatform
```

