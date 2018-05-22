import net.sf.tweety.logics.pl.syntax.*;
import org.junit.jupiter.api.Test;
import trs.Rule;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class EncodingTests {

    // testing if map of propositions is generated correctly
    @Test
    void encodingFunctionSymbolsTest() {
        PLEncoder encoder = PLEncoder.INSTANCE;
        String[] s = {"A", "S", "0"};
        HashSet<String> symbols = new HashSet<>(Arrays.asList(s));
        Map<String, Proposition> map = encoder.generatePropositions(symbols);
        String key = encoder.generateKeyPVar("A", "0");
        assertEquals("P_A,0", map.get(key).toString());
    }

    // testing correct encoding for trivial rule with only one function symbol
    @Test
    void encodingF1simple1Test() {
        PLEncoder encoder = PLEncoder.INSTANCE;
        Rule simpleRule1 = TRSGenerator.getSimpleRule1();
        ArrayList<Rule> rules = new ArrayList<>();
        rules.add(simpleRule1);
        Set<String> symbols = encoder.getFunctionSymbols(rules);
        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
        PropositionalFormula f1 = encoder.f1(symbols, propositionMap);
        PropositionalFormula expected =
                new Conjunction(
                new Negation(encoder.generatePropositionPVar("A", "A")),
                new Disjunction(encoder.generatePropositionPVar("A", "A"),
                        new Negation(encoder.generatePropositionPVar("A", "A"))));
        assertEquals(expected.toString(), f1.toString());
    }
}
