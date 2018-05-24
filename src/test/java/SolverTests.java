import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import trs.Rule;

import java.util.*;

public class SolverTests {

    @Test
    void firstTest() {
//        FSolver solver = FSolver.INSTANCE;
//        PLEncoder encoder = PLEncoder.INSTANCE;
//        List<Rule> rulesDedekind = TRSGenerator.getDedekind();
//        Set<String> symbols = encoder.getFunctionSymbols(rulesDedekind);
//        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
//        PropositionalFormula f2Dedekind = encoder.f2(rulesDedekind, propositionMap);
//
//        ArrayList<PropositionalFormula> f2DedekindList = new ArrayList<>(1);
//        f2DedekindList.add(f2Dedekind);
//        assertFalse(solver.isSatisfiable(solver.convertToDimacs(f2DedekindList).getFirst()));
    }

    @Disabled
    @Test
    void convertToDimacsTest() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rulesDedekind = TRSGenerator.getDedekind();
        Set<String> symbols = encoder.getFunctionSymbols(rulesDedekind);
        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
        PropositionalFormula f2Dedekind = encoder.f2(rulesDedekind, propositionMap);
        Conjunction f2DedekindCNF = f2Dedekind.toCnf();

        Pair<String, List<Proposition>> result = solver.convertToDimacs(f2DedekindCNF);
        assertEquals("", result.getFirst());
    }

    @Disabled
    @Test
    void dedekindF2Test() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rulesDedekind = TRSGenerator.getDedekind();
        Set<String> symbols = encoder.getFunctionSymbols(rulesDedekind);
        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
        Conjunction f2Dedekind = encoder.f2(rulesDedekind, propositionMap);

        Pair<String, List<Proposition>> conv = solver.convertToDimacs(f2Dedekind);
        List<Proposition> model = solver.getModelFromDimacs(conv.getFirst(), conv.getSecond());
        assertEquals("", model.toString());
    }

    @Test
    void dedekindFullEncodingTest() {
        FSolver solver = FSolver.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rulesDedekind = TRSGenerator.getDedekind();
        Set<String> symbols = encoder.getFunctionSymbols(rulesDedekind);
        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
        Conjunction f2 = encoder.f2(rulesDedekind, propositionMap);
        PropositionalFormula f1 = encoder.f1(symbols, propositionMap);
        Conjunction fullEncoding = new Conjunction(f1, f2).toCnf();

        Pair<String, List<Proposition>> conv = solver.convertToDimacs(fullEncoding);
        List<Proposition> model = solver.getModelFromDimacs(conv.getFirst(), conv.getSecond());
        assertEquals("", model.toString());
    }
}
