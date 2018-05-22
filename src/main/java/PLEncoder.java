import net.sf.tweety.logics.pl.syntax.*;
import trs.NonVariable;
import trs.Rule;
import trs.Term;

import java.util.*;

public class PLEncoder {

    public static final PLEncoder INSTANCE = new PLEncoder();

    private PLEncoder() {}

    PropositionalFormula getEncoding(List<Rule> rules, Set<String> symbols) {
        Map<String, Proposition> propositionMap = generatePropositions(symbols);
        PropositionalFormula f1 = f1(symbols, propositionMap);
        return f1;
    }

    PropositionalFormula f1(Set<String> functionSymbols, Map<String, Proposition> map) {
        // part A (part B is included due to the method of encoding)
        PropositionalFormula partAB = new Conjunction();
        for (String a: functionSymbols) {
            for (String b: functionSymbols) {
                Proposition p_ab = map.get(generateKeyPVar(a, b));
                Proposition p_ba = map.get(generateKeyPVar(b, a));
                Negation n = new Negation(new Conjunction(p_ab, p_ba));
                partAB = partAB.combineWithAnd(n);
            }
        }
        // part C
        PropositionalFormula partC = new Conjunction();
        for (String a: functionSymbols) {
            for (String b: functionSymbols) {
                for (String c: functionSymbols) {
                    Proposition p_ab = map.get(generateKeyPVar(a, b));
                    Proposition p_bc = map.get(generateKeyPVar(b, c));
                    Proposition p_ac = map.get(generateKeyPVar(a, c));
                    Conjunction ab_and_bc = new Conjunction(p_ab, p_bc);
                    // use 'a -> b == not a or b' to construct implication
                    Disjunction d = new Disjunction(new Negation(ab_and_bc), p_ac);
                    partC = partC.combineWithAnd(d);
                }
            }
        }
        PropositionalFormula f1 = new Conjunction(partAB, partC);
        f1 = f1.trim();
        f1 = f1.toCnf();
        return f1;
    }


    Set<String> getFunctionSymbols(List<Rule> rules) {
        Set<String> symbols = new HashSet<>();
        for (Rule r: rules) {
            Term left = r.getLeft();
            Term right = r.getRight();
            symbols.addAll(getFunctionSymbolsInTerm(left));
            symbols.addAll(getFunctionSymbolsInTerm(right));
        }
        return symbols;
    }


    Set<String> getFunctionSymbolsInTerm(Term t) {
        Set<String> symbols = new HashSet<>();
        if (t.isVariable())
            return symbols;

        NonVariable term = (NonVariable) t;
        symbols.add(term.getSymbol());
        List<Term> args = term.getArguments();
        for (Term arg: args)
            symbols.addAll(getFunctionSymbolsInTerm(arg));

        return symbols;
    }


    Map<String, Proposition> generatePropositions(Set<String> functionSymbols) {
        HashMap<String, Proposition> propositionHashMap = new HashMap<>(20);
        for (String s1: functionSymbols) {
            for (String s2: functionSymbols) {
                String key = generateKeyPVar(s1, s2);
                Proposition value = generatePropositionPVar(s1, s2);
                propositionHashMap.put(key, value);
            }
        }
        return propositionHashMap;
    }

    public String generateKeyPVar(String functionSymbolLeft, String functionSymbolRight) {
        return String.format("%s-%s", functionSymbolLeft, functionSymbolRight);
    }

    public Proposition generatePropositionPVar(String functionSymbolLeft, String functionSymbolRight) {
        String propositionalVariableName =
                String.format("P_%s,%s", functionSymbolLeft, functionSymbolRight);
        return new Proposition(propositionalVariableName);
    }
}
