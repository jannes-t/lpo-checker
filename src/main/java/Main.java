import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import trs.InvalidTRSException;
import trs.NonVariable;
import trs.Rule;
import trs.Term;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private void start(String filepath) {
        // parse the TRS
        TRSParser parser = TRSParser.INSTANCE;
        List<Rule> rules = null;
        try {
            rules = parser.constructTRS(filepath);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file");
            System.exit(1);
        } catch (InvalidTRSException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // encode the TRS
        PLEncoder encoder = PLEncoder.INSTANCE;
        PropositionalFormula encoding = encoder.getEncoding(rules);

//        ISolver solver = SolverFactory.newDefault();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please specify input file as argument");
            System.exit(1);
        }
        new Main().start(args[1]);
    }

}