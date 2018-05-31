import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

import java.util.*;

public class PrecedenceExtractor {

    static NavigableSet<String> extractPrecedenceFromModel(Set<Proposition> model, Set<String> symbols) {
        TreeSet<String> orderedSet = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.equals(s2))
                    return 0;
                Proposition p_s1s2 = PLEncoder.generatePropositionPVar(s1, s2);
                if (model.contains(p_s1s2))
                    return 1;
                else
                    return -1;
            }
        });

        orderedSet.addAll(symbols);
        return orderedSet;
    }

    static String getPrecedenceString(NavigableSet<String> ordered) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> descending = ordered.descendingIterator();
        if (descending.hasNext())
            builder.append(descending.next());
        while (descending.hasNext()) {
            builder.append(String.format(" > %s", descending.next()));
        }
        return builder.toString();
    }
}
