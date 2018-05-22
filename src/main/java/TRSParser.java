import trs.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TRSParser {

    public static final TRSParser INSTANCE = new TRSParser();
    private static final char BEG_ARGS = '(';
    private static final char END_ARGS = ')';
    private static final char DEL = '-';
    private static final char ARG_DEL = ',';

    private Scanner sc;

    private TRSParser() {
    }

    List<Rule> constructTRS(String filepath) throws FileNotFoundException, InvalidTRSException {
        Scanner input = new Scanner(new FileInputStream(filepath));
        ArrayList<Rule> rules = new ArrayList<>(10);
        while (input.hasNext()) {
            String line = input.nextLine();
            Rule rule = parseRule(line);
            rules.add(rule);
        }
        return rules;
    }

    Rule parseRule(String rule) throws InvalidTRSException {
        sc = new Scanner(rule);
        CharacterReader r = new CharacterReader(sc);
        return rule(r);
    }

    private Rule rule(CharacterReader r) throws InvalidTRSException {
        Term left = term(r);
        if (r.nextCharIs(DEL))
            r.nextChar();
        else
            throw new InvalidTRSException("wrong delimiter");
        Term right = term(r);

        r.skipWhite();
        if (r.hasNext())
            throw new InvalidTRSException("unexpected input after rule");
        return new Rule(left, right);
    }

    private Term term(CharacterReader r) throws InvalidTRSException {
        if (r.nextCharIsLowercase())
            return variable(r);
        else if (r.nextCharIsUppercase())
            return nonVariable(r);
        else
            throw new InvalidTRSException("invalid character at beginning of term");
    }

    private Variable variable(CharacterReader r) throws InvalidTRSException {
        StringBuilder name = new StringBuilder(4);
        name.append(r.nextChar());
        while (r.nextCharIsDigit())
            name.append(r.nextChar());
        return new Variable(name.toString());
    }

    private NonVariable nonVariable(CharacterReader r) throws InvalidTRSException {
        StringBuilder name = new StringBuilder(4);
        name.append(r.nextChar());
        while (r.nextCharIsDigit())
            name.append(r.nextChar());
        if (r.nextCharIs(BEG_ARGS))
            r.nextChar();
        else
            throw new InvalidTRSException("unexpected input following function symbol");

        // pass arguments
        ArrayList<Term> arguments = new ArrayList<>(2);
        boolean parsedArguments = false;
        if (r.nextCharIs(END_ARGS))
            parsedArguments = true;
        while (!parsedArguments) {
            Term argument = term(r);
            arguments.add(argument);
            if (r.nextCharIs(END_ARGS))
                parsedArguments = true;
            else if (r.nextCharIs(ARG_DEL))
                r.nextChar();
            else
                throw new InvalidTRSException("unexpected input following a term");
        }
        r.nextChar();

        return new NonVariable(name.toString(), arguments.size(), arguments);
    }

    private static class CharacterReader {

        Scanner in;

        CharacterReader(Scanner scanner) {
            in = scanner;
            in.useDelimiter("");
        }

        char nextChar() {
            return in.next().charAt(0);
        }

        boolean hasNext() {
            return in.hasNext();
        }

        boolean nextCharIs(char c) {
            return in.hasNext(Pattern.quote(Character.toString(c)));
        }

        boolean nextCharIsSpace() {
            return in.hasNext(" ");
        }

        boolean nextCharIsDigit() {
            return in.hasNext("[0-9]");
        }

        boolean nextCharIsUppercase() {
            return in.hasNext("[A-Z]");
        }

        boolean nextCharIsLowercase() {
            return in.hasNext("[a-z]");
        }

        void skipWhite() {
            while (nextCharIsSpace()) {
                in.next();
            }
        }
    }
}
