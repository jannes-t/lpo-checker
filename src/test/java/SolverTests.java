import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import trs.Rule;

import java.util.*;

@DisplayName("Processing/Solving SAT")
class SolverTests {

    @Test
    void cleanCNFTest() {
        FSolver solver = FSolver.INSTANCE;
        Disjunction clause1 = new Disjunction(new Contradiction(), new Proposition("a"));
        Disjunction clause2 = new Disjunction(new Tautology(), new Tautology());
        Disjunction clause3 = new Disjunction(new Proposition("b"), new Proposition("c"));
        List<Disjunction> cnf = Arrays.asList(clause1, clause2, clause3);
        PropositionalFormula clean = solver.cleanCNF(new Conjunction(cnf));
        if (!(clean instanceof Conjunction))
            fail("should have returned conjunction as a result of cleaning");
        else {
            Disjunction clause1Expected = new Disjunction(Arrays.asList(new Proposition("a")));
            Disjunction clause2Expected = new Disjunction(new Proposition("b"), new Proposition("c"));
            Conjunction expected = new Conjunction(clause1Expected, clause2Expected);
            assertEquals(expected, clean);
        }
    }

    @Test
    void cleanCNFTautologyTest() {
        FSolver solver = FSolver.INSTANCE;
        Disjunction clause1 = new Disjunction(new Tautology(), new Tautology());
        Disjunction clause2 = new Disjunction(new Tautology(), new Tautology());
        List<Disjunction> cnf = Arrays.asList(clause1, clause2);
        PropositionalFormula clean = solver.cleanCNF(new Conjunction(cnf));
        assertTrue(clean instanceof Tautology);
    }

    @Test
    void cleanCNFContradictionTest() {
        FSolver solver = FSolver.INSTANCE;
        Disjunction clause1 = new Disjunction(new Contradiction(), new Proposition("a"));
        Disjunction clause2 = new Disjunction(new Proposition("c"), new Proposition("a"));
        Disjunction clause3 = new Disjunction(new Contradiction(), new Contradiction());
        List<Disjunction> cnf = Arrays.asList(clause1, clause2, clause3);
        PropositionalFormula clean = solver.cleanCNF(new Conjunction(cnf));
        assertTrue(clean instanceof Contradiction);
    }

    @Test
    void convertToDimacsTest() {
        FSolver solver = FSolver.INSTANCE;
        Disjunction clause1 = new Disjunction(
                new Negation(new Proposition("a")),
                new Proposition("b"));
        Disjunction clause2 = new Disjunction(
                new Proposition("c"),
                new Proposition("d"));
        Disjunction clause3 = new Disjunction(
                Arrays.asList(
                new Proposition("b"),
                new Negation(new Proposition("c")),
                new Proposition("d")));
        Conjunction cnf = new Conjunction(Arrays.asList(clause1, clause2, clause3));
        Pair<String, List<Proposition>> result = solver.convertToDimacs(cnf);
        StringBuilder builder = new StringBuilder();
        builder.append("p cnf 4 3\n");
        builder.append("-1 2 0\n");
        builder.append("3 4 0\n");
        builder.append("2 -3 4 0\n");
        assertEquals(builder.toString(), result.getFirst());
    }

    @Test
    void dedekindF2ModelTest() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rulesDedekind = TRSBuilder.getDedekind();
        Set<String> symbols = encoder.getFunctionSymbols(rulesDedekind);
        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
        PropositionalFormula f2Dedekind = encoder.f2(rulesDedekind, propositionMap);
        Conjunction f2CNF = f2Dedekind.toCnf();

        Pair<String, List<Proposition>> conv = solver.convertToDimacs(f2CNF);
        Set<PropositionalFormula> model = solver.getModelFromDimacs(conv.getFirst(), conv.getSecond());
        // the model needs to correctly assert that A >_p S and M >_p A due to rewrite rule 2 and 4 respectively
        assertTrue(model.contains(encoder.generatePropositionPVar("M", "A")));
        assertTrue(model.contains(encoder.generatePropositionPVar("A", "S")));
    }

    @Test
    void dedekindFullEncodingModelTest() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rulesDedekind = TRSBuilder.getDedekind();
        Conjunction fullEncoding = encoder.getEncoding(rulesDedekind);

        Pair<String, List<Proposition>> conv = solver.convertToDimacs(fullEncoding);
        Set<PropositionalFormula> model = solver.getModelFromDimacs(conv.getFirst(), conv.getSecond());
        // the model needs to correctly establish M >_p A >_p S (Z will be randomly inserted)
        assertTrue(model.contains(encoder.generatePropositionPVar("M", "A")));
        assertTrue(model.contains(encoder.generatePropositionPVar("M", "S")));
        assertTrue(model.contains(encoder.generatePropositionPVar("A", "S")));
    }

    @Test
    void ackermannFullEncodingModelTest() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rulesAckermann = TRSBuilder.getAckermann();
        Conjunction fullEncoding = encoder.getEncoding(rulesAckermann);

        Pair<String, List<Proposition>> conv = solver.convertToDimacs(fullEncoding);
        Set<PropositionalFormula> model = solver.getModelFromDimacs(conv.getFirst(), conv.getSecond());
        // the model needs to correctly establish M >_p A >_p S (Z will be randomly inserted)
        assertTrue(model.contains(encoder.generatePropositionPVar("A", "S")));
    }

    @Test
    void dersh5aFullEncodingModelTest() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> dersh5a = TRSBuilder.getDersh5a();
        Conjunction fullEncoding = encoder.getEncoding(dersh5a);

        Pair<String, List<Proposition>> conv = solver.convertToDimacs(fullEncoding);
        Set<PropositionalFormula> model = solver.getModelFromDimacs(conv.getFirst(), conv.getSecond());
        // the model needs to correctly establish M >_p A >_p S (Z will be randomly inserted)
        assertTrue(model.contains(encoder.generatePropositionPVar("N", "A")));
        assertTrue(model.contains(encoder.generatePropositionPVar("N", "O")));
        assertTrue(model.contains(encoder.generatePropositionPVar("A", "O")));
    }
}
