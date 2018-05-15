import org.junit.jupiter.api.Test;
import trs.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTRSTests {

    @Test
    void simpleParseRuleTest() {
        Parser p = new Parser();
        Rule result = null;
        try {
            result = p.parseRule("A(x)-x");
        } catch (InvalidTRSException e) {

        }
        Term x = new Variable("x");
        ArrayList<Term> leftArgs = new ArrayList<>();
        leftArgs.add(x);
        Rule expected = new Rule(new NonVariable("A", 1, leftArgs), x);

        assertEquals(expected.toString(), result.toString());
    }
}
