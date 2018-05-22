import org.junit.jupiter.api.Test;
import trs.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTRSTests {

    @Test
    void simpleParseRuleTest() {
        TRSParser p = TRSParser.INSTANCE;
        Rule result = null;
        try {
            result = p.parseRule("A(x)-x");
        } catch (InvalidTRSException e) {
            fail("Valid syntax threw invalid syntax exception");
        }
        Rule expected = TRSGenerator.getSimpleRule1();

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    void simpleParseRuleTest2() {
        TRSParser p = TRSParser.INSTANCE;
        Rule result = null;
        try {
            result = p.parseRule("A(x,S(y))-A(S(x),y)");
        } catch (InvalidTRSException e) {
            fail("Valid syntax threw invalid syntax exception");
        }
        Rule expected = TRSGenerator.getSimpleRule2();

        assertEquals(expected.toString(), result.toString());
    }
}
