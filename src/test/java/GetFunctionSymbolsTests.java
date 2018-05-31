import org.junit.jupiter.api.Test;
import trs.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GetFunctionSymbolsTests {

    @Test
    void getSymbolsFromTermTest() {
        Rule r = null;
        try {
            r = TRSParser.parseRule("A(x,Z())->x");
        } catch(InvalidTRSException e) {
            fail("invalid syntax exception for a valid syntax rule");
        }
        Set<String> result = PLEncoder.getFunctionSymbolsInTerm(r.getLeft());
        Set<String> expected = new HashSet<>();
        expected.add("A");
        expected.add("Z");

        assertEquals(expected, result);
    }
}
