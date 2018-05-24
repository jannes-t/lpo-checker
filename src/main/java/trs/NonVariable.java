package trs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NonVariable extends Term{

    private int arity;
    private String symbol;
    private List<Term> arguments;

    public NonVariable(String symbol, int arity, List<Term> arguments) {
        this.symbol = symbol;
        this.arity = arity;
        if (arity > 0) {
            this.arguments = arguments;
        }
        else {
            this.arguments = new ArrayList<>();
        }
    }

    public int getArity() {
        return arity;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<Term> getArguments() {
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
            argumentList = builder.toString();
        }
        return String.format("%s(%s)", symbol, argumentList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NonVariable that = (NonVariable) o;
        return arity == that.arity &&
                symbol.equals(that.getSymbol()) &&
                arguments.equals(that.getArguments());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(arity);
        result = 31 * result + symbol.hashCode();
        for (Term arg: arguments)
            result = 31 * result + arg.hashCode();
        return result;
    }
}
