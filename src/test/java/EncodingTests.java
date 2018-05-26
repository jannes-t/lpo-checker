import net.sf.tweety.logics.pl.syntax.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import trs.InvalidTRSException;
import trs.Rule;
import trs.Term;
import trs.Variable;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.*;

@DisplayName("Encoding to SAT")
class EncodingTests {

    @BeforeAll
    static void initAll() {

    }

    // testing if map of propositions is generated correctly
    @DisplayName("generating P variables from function symbols")
    @Test
    void encodingFunctionSymbolsTest() {
        PLEncoder encoder = PLEncoder.INSTANCE;
        String[] s = {"A", "S", "Z"};
        HashSet<String> symbols = new HashSet<>(Arrays.asList(s));
        Map<String, Proposition> map = encoder.generatePropositions(symbols);
        String key = encoder.generateKeyPVar("A", "Z");
        assertEquals("P_A,Z", map.get(key).toString());
    }

    // testing correct encoding for trivial rule with only two function symbols
    @Disabled
    @DisplayName("F1 encoding of two function symbols")
    @Test
    void encodingF1simple1Test() {
        PLEncoder encoder = PLEncoder.INSTANCE;
        Rule simpleRule2 = TRSBuilder.getSimpleRule2();
        ArrayList<Rule> rules = new ArrayList<>();
        rules.add(simpleRule2);
        Set<String> symbols = encoder.getFunctionSymbols(rules);
        Map<String, Proposition> propositionMap = encoder.generatePropositions(symbols);
        PropositionalFormula f1 = encoder.f1(symbols, propositionMap);

        PropositionalFormula expectedPartAB =
                new Negation(
                        new Conjunction(
                                encoder.generatePropositionPVar("A", "A"),
                                encoder.generatePropositionPVar("A", "A")
                        )
                );
        PropositionalFormula expectedPartC =
                new Disjunction(
                        encoder.generatePropositionPVar("A", "A"),
                        new Negation(
                                new Conjunction(
                                        encoder.generatePropositionPVar("A", "A"),
                                        encoder.generatePropositionPVar("A", "A"))
                        )
                );
        PropositionalFormula expected =
                new Conjunction(expectedPartAB, expectedPartC).trim();

        assertEquals(expected.toString(), f1.toString());
    }

    // test if method to check if variable is element in term is correct
    @Test
    void varInTermTest() {
        PLEncoder encoder = PLEncoder.INSTANCE;
        Rule r = TRSBuilder.getSimpleRule2();
        Term t = r.getLeft();
        Variable v = new Variable("y");
        assertTrue(encoder.isVariableInTerm(t, v));
    }

    @Test
    void varNotInTermTest() {
        PLEncoder encoder = PLEncoder.INSTANCE;
        Rule r = TRSBuilder.getSimpleRule2();
        Term t = r.getLeft();
        Variable v = new Variable("z");
        assertFalse(encoder.isVariableInTerm(t, v));
    }

    @Disabled
    @Test
    void encodingF2DedekindTest() {
        TRSParser p = TRSParser.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        List<Rule> rules = null;
        try {
            rules = p.constructTRS("src/test/resources/mDedekind.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file");
            System.exit(1);
            fail("wrong filepath");
        } catch (InvalidTRSException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            fail("syntax error in correct trs");
        }
        Set<String> symbols = encoder.getFunctionSymbols(rules);
        Set<String> expectedSymbols = new HashSet<>();
        expectedSymbols.add("A");
        expectedSymbols.add("S");
        expectedSymbols.add("M");
        expectedSymbols.add("Z");
        assertEquals(expectedSymbols, symbols);

        Map<String, Proposition> map = encoder.generatePropositions(symbols);

        Rule rule2 = rules.get(1);
        ArrayList<Rule> onlyRule2 = new ArrayList<>();
        onlyRule2.add(rule2);
        PropositionalFormula f2 = encoder.f2(onlyRule2, map);
        assertEquals(new Proposition("x"), f2);
    }
}
