import net.sf.tweety.logics.pl.syntax.*;
import trs.NonVariable;
import trs.Rule;
import trs.Term;
import trs.Variable;

import java.util.*;

public class PLEncoder {

    public static final PLEncoder INSTANCE = new PLEncoder();

    private PLEncoder() {}

    PropositionalFormula getEncoding(List<Rule> rules) {
        Set<String> symbols = getFunctionSymbols(rules);
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

    Conjunction f2(List<Rule> rules, Map<String, Proposition> map) {
        List<PropositionalFormula> conjuncts = new ArrayList<>();
        for (Rule rule: rules) {
            PropositionalFormula r_ts = r_ts(rule.getLeft(), rule.getRight(), map);
            // add check if r_ts == false to improve performance
            conjuncts.add(r_ts);
        }
        PropositionalFormula result = new Conjunction(conjuncts);
        result = result.trim();
        return result.toCnf();
    }

    PropositionalFormula r_ts(Term t, Term s, Map<String, Proposition> map) {
        PropositionalFormula result = new Disjunction();
        // if t in r = t -> s is a variable TRS is non-terminating / rule is illegal
        if (t.isVariable())
            return new Contradiction();
        if (s.isVariable()) {
            Variable rightVar = (Variable) s;
            // LPO1
            if (isVariableInTerm(t, rightVar)) {
                return new Tautology();
            }
            // if s not in Var(t) rule is non-terminating / illegal
            else {
                return new Contradiction();
            }
        }

        // LPO2 t and s must both be terms with function symbols
        NonVariable left = (NonVariable) t;
        NonVariable right = (NonVariable) s;
        List<Term> leftArgs = left.getArguments();
        List<Term> rightArgs = right.getArguments();
        // 2A
        // check if any t_i = s
        for (Term arg: leftArgs) {
            if (arg.equals(right)) {
                return new Tautology();
            }
        }
        // OR r_ti_s
        Disjunction d = new Disjunction();
        for (Term arg: leftArgs) {
            d = d.combineWithOr(r_ts(arg, right, map));
        }
        result = result.combineWithOr(d);

        String F = left.getSymbol();
        String G = right.getSymbol();
        if (!F.equals(G)) {
            //2B F != G
            Proposition p_fg = map.get(generateKeyPVar(F, G));
            PropositionalFormula lpo2b = p_fg.combineWithAnd(andR_ts_j(left, right, map));
            result = result.combineWithOr(lpo2b);
        }
        else {
            //2C F == G
            // loop through args to find the first two arguments of same index that are unequal
            int maxArity = Math.max(leftArgs.size(), rightArgs.size());
            for (int i = 0; i < maxArity; i++) {
                if (!leftArgs.get(i).equals(rightArgs.get(i))) {
                    Term t_i = leftArgs.get(i);
                    Term s_i = rightArgs.get(i);
                    PropositionalFormula r_tisi = r_ts(t_i, s_i, map);
                    PropositionalFormula lpo2c = r_tisi.combineWithAnd(andR_ts_j(left, right, map));
                    result = result.combineWithOr(lpo2c);
                    break;
                }
            }
            // else no need to add anything
        }

        return result;
    }

    PropositionalFormula andR_ts_j(NonVariable t, NonVariable s, Map<String, Proposition> map) {
        PropositionalFormula result = new Conjunction();
        for (Term arg: s.getArguments()) {
            result = result.combineWithAnd(r_ts(t, arg, map));
        }
        return result;
    }

    boolean isVariableInTerm(Term term, Variable variable) {
        if (term.isVariable()) {
            Variable termVariable = (Variable) term;
            return variable.equals(termVariable);
        }
        NonVariable actualTerm = (NonVariable) term;
        boolean result = false;
        for (Term subterm: actualTerm.getArguments()) {
            result |= isVariableInTerm(subterm, variable);
        }
        return result;
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

    String generateKeyPVar(String functionSymbolLeft, String functionSymbolRight) {
        return String.format("%s-%s", functionSymbolLeft, functionSymbolRight);
    }

    Proposition generatePropositionPVar(String functionSymbolLeft, String functionSymbolRight) {
        String propositionalVariableName =
                String.format("P_%s,%s", functionSymbolLeft, functionSymbolRight);
        return new Proposition(propositionalVariableName);
    }
}
