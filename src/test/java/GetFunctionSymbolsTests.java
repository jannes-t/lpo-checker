import org.junit.jupiter.api.Test;
import trs.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GetFunctionSymbolsTests {

    @Test
    void getSymbolsFromTermTest() {
        TRSParser parser = TRSParser.INSTANCE;
        PLEncoder encoder = PLEncoder.INSTANCE;
        Rule r = null;
        try {
            r = parser.parseRule("A(x,Z())-x");
        } catch(InvalidTRSException e) {
            fail("invalid syntax exception for a valid syntax rule");
        }
        Set<String> result = encoder.getFunctionSymbolsInTerm(r.getLeft());
        Set<String> expected = new HashSet<>();
        expected.add("A");
        expected.add("Z");

        assertEquals(expected, result);
    }
}
