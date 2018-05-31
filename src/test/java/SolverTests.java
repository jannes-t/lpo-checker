import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.TimeoutException;
import trs.Rule;

import java.io.IOException;
import java.util.*;

@DisplayName("Processing/Solving SAT")
class SolverTests {


    @Test
    void cleanCNFTest() {
        Disjunction clause1 = new Disjunction(new Contradiction(), new Proposition("a"));
        Disjunction clause2 = new Disjunction(new Tautology(), new Tautology());
        Disjunction clause3 = new Disjunction(new Proposition("b"), new Proposition("c"));
        List<Disjunction> cnf = Arrays.asList(clause1, clause2, clause3);
        PropositionalFormula clean = FSolver.cleanCNF(new Conjunction(cnf));
        if (!(clean instanceof Conjunction))
            fail("should have returned conjunction as a result of cleaning");
        else {
            Disjunction clause1Expected = new Disjunction(Collections.singletonList(new Proposition("a")));
            Disjunction clause2Expected = new Disjunction(new Proposition("b"), new Proposition("c"));
            Conjunction expected = new Conjunction(clause1Expected, clause2Expected);
            assertEquals(expected, clean);
        }
    }

    @Test
    void cleanCNFTautologyTest() {
        Disjunction clause1 = new Disjunction(new Tautology(), new Tautology());
        Disjunction clause2 = new Disjunction(new Tautology(), new Tautology());
        List<Disjunction> cnf = Arrays.asList(clause1, clause2);
        PropositionalFormula clean = FSolver.cleanCNF(new Conjunction(cnf));
        assertTrue(clean instanceof Tautology);
    }

    @Test
    void cleanCNFContradictionTest() {
        Disjunction clause1 = new Disjunction(new Contradiction(), new Proposition("a"));
        Disjunction clause2 = new Disjunction(new Proposition("c"), new Proposition("a"));
        Disjunction clause3 = new Disjunction(new Contradiction(), new Contradiction());
        List<Disjunction> cnf = Arrays.asList(clause1, clause2, clause3);
        PropositionalFormula clean = FSolver.cleanCNF(new Conjunction(cnf));
        assertTrue(clean instanceof Contradiction);
    }

    @Test
    void convertToDimacsTest() {
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
        Pair<String, List<Proposition>> result = FSolver.convertToDimacs(cnf);
        StringBuilder builder = new StringBuilder();
        builder.append("p cnf 4 3\n");
        builder.append("-1 2 0\n");
        builder.append("3 4 0\n");
        builder.append("2 -3 4 0\n");
        assertEquals(builder.toString(), result.getFirst());
    }

    @Test
    void dedekindF2ModelTest() {
        List<Rule> rulesDedekind = TRSBuilder.getDedekind();
        Set<String> symbols = PLEncoder.getFunctionSymbols(rulesDedekind);
        Map<String, Proposition> propositionMap = PLEncoder.generatePropositions(symbols);
        PropositionalFormula f2Dedekind = PLEncoder.f2(rulesDedekind, propositionMap);
        Conjunction f2CNF = f2Dedekind.toCnf();

        Pair<String, List<Proposition>> conv = FSolver.convertToDimacs(f2CNF);
        Set<Proposition> model = null;
        try {
            model = FSolver.getModelFromDimacs(conv.getFirst(), conv.getSecond()).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
        // the model needs to correctly assert that A >_p S and M >_p A due to rewrite rule 2 and 4 respectively
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("M", "A")));
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("A", "S")));
    }

    @Test
    void dedekindFullEncodingModelTest() {
        List<Rule> rulesDedekind = TRSBuilder.getDedekind();
        Conjunction fullEncoding = PLEncoder.getEncoding(rulesDedekind);

        Pair<String, List<Proposition>> conv = FSolver.convertToDimacs(fullEncoding);
        Set<Proposition> model = null;
        try {
            model = FSolver.getModelFromDimacs(conv.getFirst(), conv.getSecond()).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
        // the model needs to correctly establish M >_p A >_p S (Z will be randomly inserted)
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("M", "A")));
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("M", "S")));
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("A", "S")));
    }

    @Test
    void ackermannFullEncodingModelTest() {
        List<Rule> rulesAckermann = TRSBuilder.getAckermann();
        Conjunction fullEncoding = PLEncoder.getEncoding(rulesAckermann);

        Pair<String, List<Proposition>> conv = FSolver.convertToDimacs(fullEncoding);
        Set<Proposition> model = null;
        try {
            model = FSolver.getModelFromDimacs(conv.getFirst(), conv.getSecond()).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
        // the model needs to correctly establish M >_p A >_p S (Z will be randomly inserted)
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("A", "S")));
    }

    @Test
    void dersh5aFullEncodingModelTest() {
        List<Rule> dersh5a = TRSBuilder.getDersh5a();
        Conjunction fullEncoding = PLEncoder.getEncoding(dersh5a);

        Pair<String, List<Proposition>> conv = FSolver.convertToDimacs(fullEncoding);
        Set<Proposition> model = null;
        try {
            model = FSolver.getModelFromDimacs(conv.getFirst(), conv.getSecond()).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
        // the model needs to correctly establish M >_p A >_p S (Z will be randomly inserted)
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("N", "A")));
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("N", "O")));
        assertTrue(model.contains(PLEncoder.generatePropositionPVar("A", "O")));
    }
}
