import org.junit.jupiter.api.Test;
import trs.*;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTRSTests {

    @Test
    void simpleParseRuleTest() {
        Rule result = null;
        try {
            result = TRSParser.parseRule("A(x)->x");
        } catch (InvalidTRSException e) {
            fail("Valid syntax threw invalid syntax exception");
        }
        Rule expected = TRSBuilder.getSimpleRule1();

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void simpleParseRuleTest2() {
        Rule result = null;
        try {
            result = TRSParser.parseRule("A(x,S(y))->A(S(x),y)");
        } catch (InvalidTRSException e) {
            fail("Valid syntax threw invalid syntax exception");
        }
        Rule expected = TRSBuilder.getSimpleRule2();

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void parseDedekindTest() {
        List<Rule> rules = null;
        try {
            rules = TRSParser.constructTRS("src/test/resources/mDedekind.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file");
            System.exit(1);
            fail("wrong filepath");
        } catch (InvalidTRSException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            fail("syntax error in correct trs");
        }
        assertEquals("A(Z(),x)-x", rules.get(0).toString());
        assertEquals("A(S(x),y)-A(x,S(y))", rules.get(1).toString());
        assertEquals("M(x,Z())-Z()", rules.get(2).toString());
        assertEquals("M(x,S(y))-A(x,M(x,y))", rules.get(3).toString());
    }
}
