package chatbot;

import java.util.regex.*;

public class MathEvaluator {

    private static final Pattern SYMBOL_EXPR = Pattern.compile(
        "(\\d+(?:\\.\\d+)?)\\s*([+\\-*/^%])\\s*(\\d+(?:\\.\\d+)?)"
    );
    private static final Pattern WORD_EXPR = Pattern.compile(
        "(\\d+(?:\\.\\d+)?)\\s*(plus|minus|times|multiplied by|divided by|mod|to the power of)\\s*(\\d+(?:\\.\\d+)?)",
        Pattern.CASE_INSENSITIVE
    );

    public String evaluate(String input) {
        try {
            Matcher m = SYMBOL_EXPR.matcher(input);
            if (m.find()) return compute(Double.parseDouble(m.group(1)), m.group(2), Double.parseDouble(m.group(3)));
            m = WORD_EXPR.matcher(input);
            if (m.find()) return compute(Double.parseDouble(m.group(1)), wordToOp(m.group(2).toLowerCase()), Double.parseDouble(m.group(3)));
        } catch (Exception ignored) {}
        return null;
    }

    private String wordToOp(String w) {
        switch (w) {
            case "plus":           return "+";
            case "minus":          return "-";
            case "times":
            case "multiplied by":  return "*";
            case "divided by":     return "/";
            case "mod":            return "%";
            case "to the power of":return "^";
            default:               return "+";
        }
    }

    private String compute(double a, String op, double b) {
        double result;
        switch (op) {
            case "+": result = a + b; break;
            case "-": result = a - b; break;
            case "*": result = a * b; break;
            case "/":
                if (b == 0) return "Cannot divide by zero!";
                result = a / b; break;
            case "%": result = a % b; break;
            case "^": result = Math.pow(a, b); break;
            default: return null;
        }
        String r = result == Math.floor(result)
            ? String.valueOf((long) result)
            : String.format("%.4f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
        String aStr = a == Math.floor(a) ? String.valueOf((long) a) : String.valueOf(a);
        String bStr = b == Math.floor(b) ? String.valueOf((long) b) : String.valueOf(b);
        return String.format("%s %s %s = %s", aStr, op, bStr, r);
    }
}
