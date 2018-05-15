package trs;

import java.util.ArrayList;

public class NonVariable extends Term{

    private int arity;
    private String symbol;
    private ArrayList<Term> arguments;

    public NonVariable(String symbol, int arity, ArrayList<Term> arguments) {
        this.symbol = symbol;
        this.arity = arity;
        if (arity > 0) {
            this.arguments = arguments;
        }
        else {
            this.arguments = null;
        }
    }

    public int getArity() {
        return arity;
    }

    public String getSymbol() {
        return symbol;
    }

    public ArrayList<Term> getArguments() {
        return arguments;
    }

    public boolean isVariable() {
        return false;
    }

    @Override
    public String toString() {
        String argumentList = "";
        if (arity > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(arguments.get(0).toString());
            for (int i = 1; i < arguments.size(); i++)
                builder.append("," + arguments.get(i).toString());
        }
        return String.format("%s(%s)", symbol, argumentList);
    }
}
