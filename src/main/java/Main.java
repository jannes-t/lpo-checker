import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import trs.InvalidTRSException;
import trs.Rule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private ArrayList<Rule> constructTRS(String filepath) {
        Scanner input = null;
        try {
            input = new Scanner(new FileInputStream(filepath));
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file");
            System.exit(1);
        }

        ArrayList<Rule> rules = new ArrayList<>(10);
        while (input.hasNext()) {
            String line = input.nextLine();
            try {
                Rule rule = new Parser().parseRule(line);
                rules.add(rule);
            } catch (InvalidTRSException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        return rules;
    }

    private void start(String filepath) {
        ArrayList<Rule> rules = constructTRS(filepath);

        ISolver solver = SolverFactory.newDefault();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please specify input file as argument");
            System.exit(1);
        }
        new Main().start(args[1]);
    }

}