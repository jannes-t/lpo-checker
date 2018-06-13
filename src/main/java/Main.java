import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import org.apache.commons.cli.*;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.TimeoutException;
import trs.InvalidTRSException;
import trs.Rule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {

    private void start(CommandLine line) {
        String filepath = line.getArgs()[0];
        // parse the TRS
        List<Rule> rules = null;
        try {
            rules = TRSParser.constructTRS(filepath);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file");
            System.exit(1);
        } catch (InvalidTRSException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // encode the TRS
        PropositionalFormula encoding = PLEncoder.getEncoding(rules);
        Set<Proposition> model = null;
        try {
            Pair<Boolean, Set<Proposition>> modelPair = FSolver.getModelOrNull(encoding.toCnf());
            if (modelPair.getFirst()) {
                System.out.println("is lpo terminating");
                model = modelPair.getSecond();
            }
            else {
                System.out.println("is not lpo-terminating");
                return;
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ParseFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (line.hasOption("p")) {
            System.out.println("possible precedence:");
            if (model == null) {
                System.out.println("any order on function symbols possible (trivially lpo-terminating)");
            }
            else {
                Set<String> symbols = PLEncoder.getFunctionSymbols(rules);
                System.out.println(PrecedenceExtractor.getPrecedenceString(
                        PrecedenceExtractor.extractPrecedenceFromModel(model, symbols)));
            }
        }
    }

    public static void main(String[] args) {
        // add command line options
        Option timeout = Option.builder("t")
                .argName("minutes")
                .hasArg()
                .desc("timeout after specified interval")
                .longOpt("timeout")
                .build();
        Option help = Option.builder("h")
                .desc("print help message")
                .longOpt("help")
                .build();
        Option precedence = new Option("p", "print one possible precedence if lpo-terminating");

        Options options = new Options();
        options.addOption(timeout);
        options.addOption(precedence);
        options.addOption(help);

        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        // check if given options are valid
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing of options failed.  Reason: " + e.getMessage());
            helpFormatter.printHelp("lpochecker [Options] ... TRS-filepath", options);
            System.exit(1);
        }

        // check if filepath is given
        if (line.getArgs().length != 1) {
            System.out.println("Please specify input file as argument");
            helpFormatter.printHelp("lpochecker [Options] ... TRS-filepath", options);
            System.exit(1);
        }

        new Main().start(line);
    }

}