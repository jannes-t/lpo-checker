import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.*;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class FSolver {

    private FSolver() {}

    /**
     *
     * @param cnf A formula in cnf
     * @return    If satisfiable: true,
     *                A set of literals which satisfy the given formula / null if trivially true
     *            If unsatisfiable: false, null
     */
    static Pair<Boolean, Set<Proposition>> getModelOrNull(Conjunction cnf) throws
            TimeoutException, ParseFormatException, IOException {

        PropositionalFormula cleanCNF = cleanCNF(cnf);
        if (cleanCNF instanceof Tautology)
            return new Pair<>(true, null);
        else if (cleanCNF instanceof Contradiction)
            return new Pair<>(false, null);

        // casting is safe because cleanCNF method must return Conjunction in this case
        Conjunction cnfProcessed = (Conjunction) cleanCNF;

        Pair<String, List<Proposition>> dimacsMappingPair = convertToDimacs(cnfProcessed);
        String dimacsString = dimacsMappingPair.getFirst();
        List<Proposition> propositionsMapping = dimacsMappingPair.getSecond();

        Optional<Set<Proposition>> optModel = getModelFromDimacs(dimacsString, propositionsMapping);
        if (optModel.isPresent())
            return new Pair<>(true, optModel.get());
        else
            return new Pair<>(false, null);
    }

    /**
     *
     * @param dimacsString String of SAT-problem in dimacs format
     * @param mapping      List of propositions, where each propositions index+1 maps to propositions in the dimacs format
     * @return             If satisfiable: A set of literals which satisfy the given formula
     *                     If unsatisfiable: null
     */
    static Optional<Set<Proposition>> getModelFromDimacs(String dimacsString, List<Proposition> mapping) throws
            TimeoutException, ParseFormatException, IOException {

        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(solver);
        // CNF filename is given on the command line
        try {
            IProblem problem = reader.parseInstance(
                    new ByteArrayInputStream(dimacsString.getBytes(Charset.defaultCharset())));
            if (problem.isSatisfiable()) {
                int[] model = problem.model();
                Set<Proposition> result = new HashSet<>(model.length);
                for (int i: model) {
                    if (i > 0)
                        result.add(mapping.get(i-1));
//                    else
//                        result.add(new Negation(mapping.get(-i-1)));
                }
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        } catch (ContradictionException e) {
            return Optional.empty();
        } catch (TimeoutException e) {
            throw new TimeoutException("Timeout occurred");
        }
    }

    /**
     * This function will throw RuntimeExceptions if the given cnf is trivially true/false
     * @param cnf Formula in cnf which is not trivially false (contains trivially false disjunction)
     *            and also not trivially true (all disjunctions are Tautologies)
     *
     * @return     Pair of:
     *             -String of SAT-problem in Dimacs Format
     *             -List of propositions, where each propositions index+1 maps to propositions in the dimacs format
     */
    static Pair<String, List<Proposition>> convertToDimacs(Conjunction cnf) {
        List<Proposition> props = new ArrayList<>();
        props.addAll(cnf.getAtoms());

        // as conj is in CNF all formulas should be disjunctions
        // filter out all trivially true clauses (ones containing tautologies)
        List<Disjunction> clauses = cnf.stream()
                .map(c -> (Disjunction) c)
                .filter(d -> !isTriviallyTrueDisjunction(d))
                .collect(Collectors.toList());

        if (clauses.isEmpty())
            throw new RuntimeException("This should not happen, trivially true cnf should be resolved earlier");

        StringBuilder builder = new StringBuilder();
        String header = "p cnf " + props.size() + " " + clauses.size() + "\n";
        builder.append(header);

        for (Disjunction disj : clauses) {
            if (isContradictiveDisjunction(disj))
                throw new RuntimeException("This should not happen, trivially false cnf should be resolved earlier");

            for (PropositionalFormula p : disj) {
                if (p instanceof Proposition) {
                    builder.append(props.indexOf(p) + 1);
                    builder.append(" ");
                } else if (p instanceof Negation) {
                    Proposition proposition = (Proposition) ((Negation) p).getFormula();
                    builder.append(String.format("-%s ", props.indexOf(proposition) + 1));
                } else if (p instanceof Contradiction) {
                    // ignore single contradictions
                } else {
                    String s = p.getClass().toString();
                    throw new RuntimeException("Should not happen, wrong format: " + s + ", but expected literal");
                }
            }
            builder.append("0\n");

        }
        return new Pair<>(builder.toString(), props);
    }

    /**
     * Returns a cnf formula free of Contradiction and Tautology literals,
     * or Contradiction, Tautology if trivially false or true
     * @param cnf A conjunction in conjunctive normal form
     * @return    If cnf is trivially contradictory returns a Contradiction
     *            If cnf is trivially true returns a Tautology
     *            Else returns a Conjunction in cnf, which neither contains Contradictions nor Tautologies as literals
     */
    static PropositionalFormula cleanCNF(Conjunction cnf) {
        boolean isTriviallyContradictory = cnf.stream()
                .map(d -> (Disjunction) d)
                .anyMatch(FSolver::isContradictiveDisjunction);

        if (isTriviallyContradictory)
            return new Contradiction();

        // filter out all trivially true disjunctions (as a result all tautology literals are removed)
        // filter out all contradiction literals from each disjunction
        List<Disjunction> clauses = cnf.stream()
                .map(c -> (Disjunction) c)
                .filter(d -> !isTriviallyTrueDisjunction(d))
                .map(d -> d.stream()
                        .filter(e -> !(e instanceof Contradiction))
                        .collect(Collectors.toList()))
                .map(Disjunction::new)
                .collect(Collectors.toList());

        if (clauses.isEmpty())
            return new Tautology();

        return new Conjunction(clauses);
    }


    static boolean isTriviallyTrueDisjunction(Disjunction disjunction) {
        for (PropositionalFormula disjunct : disjunction) {
            if (disjunct instanceof Tautology)
                return true;
        }
        return false;
    }

    static boolean isContradictiveDisjunction(Disjunction disjunction) {
        for (PropositionalFormula disjunct : disjunction) {
            if (!(disjunct instanceof Contradiction))
                return false;
        }
        return true;
    }
}
