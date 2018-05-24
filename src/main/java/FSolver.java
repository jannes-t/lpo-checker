import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.SatReasoner;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class FSolver {
    public static final FSolver INSTANCE = new FSolver();

    private Sat4jSolver satSolver;

    private FSolver() {
        satSolver = new Sat4jSolver();
    }

    boolean isSatisfiable(String dimacsString) {
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(solver);
        PrintWriter out = new PrintWriter(System.out, true);
        // CNF filename is given on the command line
        try {
            IProblem problem = reader.parseInstance(
                    new ByteArrayInputStream(dimacsString.getBytes(Charset.defaultCharset())));
            if (problem.isSatisfiable()) {
                System.out.println("Satisfiable !");
                reader.decode(problem.model(), out);
                return true;
            } else {
                System.out.println("Unsatisfiable !");
                return false;
            }
        } catch (FileNotFoundException e) {
            //
        } catch (ParseFormatException e) {
            //
        } catch (IOException e) {
            //
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        } catch (TimeoutException e) {
            System.out.println("Timeout, sorry!");
        }
        return false;
    }

    boolean test(PropositionalFormula f) {
        ArrayList<PropositionalFormula> fs = new ArrayList<>(1);
        fs.add(f);
        Pair<String, List<PropositionalFormula>> pair = convertToDimacs(fs);
        return isSatisfiable(pair.getFirst());
    }

    List<Proposition> getModelFromDimacs(String dimacsString, List<Proposition> mapping) {
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(solver);
        PrintWriter out = new PrintWriter(System.out, true);
        // CNF filename is given on the command line
        try {
            IProblem problem = reader.parseInstance(
                    new ByteArrayInputStream(dimacsString.getBytes(Charset.defaultCharset())));
            if (problem.isSatisfiable()) {
                int[] model = problem.model();
                List<Proposition> result = new ArrayList<>(model.length);
                for (int i: model) {
                    if (i > 0)
                        result.add(mapping.get(i-1));
                }
                return result;
            } else {
                return null;
            }
        } catch (ParseFormatException e) {
            //
        } catch (IOException e) {
            //
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        } catch (TimeoutException e) {
            System.out.println("Timeout, sorry!");
        }
        return null;
    }


    Pair<String, List<Proposition>> convertToDimacs(Conjunction conj) {
        Conjunction cnf = conj.toCnf();
        List<Proposition> props = new ArrayList<>();
        props.addAll(cnf.getAtoms());

        // as conj is in CNF all formulas should be disjunctions
        // filter out all trivially true clauses (ones containing tautologies)
        List<Disjunction> clauses = cnf.stream()
                .map(c -> (Disjunction) c)
                .filter(d -> !isTriviallyTrueDisjunction(d))
                .collect(Collectors.toList());

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


    boolean isTriviallyTrueDisjunction(Disjunction disjunction) {
        for (PropositionalFormula disjunct : disjunction) {
            if (disjunct instanceof Tautology)
                return true;
        }
        return false;
    }

    boolean isContradictiveDisjunction(Disjunction disjunction) {
        for (PropositionalFormula disjunct : disjunction) {
            if (!(disjunct instanceof Contradiction))
                return false;
        }
        return true;
    }


    // copied from SatSolver class in tweety library
    Pair<String, List<PropositionalFormula>> convertToDimacs(Collection<PropositionalFormula> formulas) {
        List<Proposition> props = new ArrayList<Proposition>();
        for (PropositionalFormula f : formulas) {
            props.removeAll(f.getAtoms());
            props.addAll(f.getAtoms());
        }
        List<PropositionalFormula> clauses = new ArrayList<PropositionalFormula>();
        List<PropositionalFormula> mappings = new ArrayList<PropositionalFormula>();
        for (PropositionalFormula p : formulas) {
            Conjunction pcnf = p.toCnf();
            for (PropositionalFormula sub : pcnf) {
                clauses.add(sub);
                mappings.add(p);
            }
        }
        String s = "p cnf " + props.size() + " " + clauses.size() + "\n";
        for (PropositionalFormula p1 : clauses) {
            // as conj is in CNF all formulas should be disjunctions
            Disjunction disj = (Disjunction) p1;
            for (PropositionalFormula p2 : disj) {
                if (p2 instanceof Proposition)
                    s += (props.indexOf(p2) + 1) + " ";
                else if (p2 instanceof Negation) {
                    s += "-" + (props.indexOf((Proposition) ((Negation) p2).getFormula()) + 1) + " ";
                } else throw new RuntimeException("This should not happen: formula is supposed to " +
                        "be in CNF but another formula than a literal has been encountered.");
            }
            s += "0\n";
        }
        return new Pair<>(s, mappings);
    }
}
