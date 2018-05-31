import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import org.junit.jupiter.api.Test;
import trs.Rule;

import java.util.List;
import java.util.NavigableSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PrecedenceTests {

    @Test
    void precedenceDedekindTest() {
        List<Rule> rulesDedekind = TRSBuilder.getDedekind();
        Conjunction fullEncoding = PLEncoder.getEncoding(rulesDedekind);
        Set<String> symbols = PLEncoder.getFunctionSymbols(rulesDedekind);

        Set<Proposition> model = null;
        try {
            model = FSolver.getModelOrNull(fullEncoding).getSecond();
        } catch (Exception e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
        NavigableSet<String> pSet = PrecedenceExtractor.extractPrecedenceFromModel(model, symbols);
        assertEquals("", PrecedenceExtractor.getPrecedenceString(pSet));
    }
}
