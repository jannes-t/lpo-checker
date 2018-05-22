import trs.NonVariable;
import trs.Rule;
import trs.Term;
import trs.Variable;

import java.util.ArrayList;

public class TRSGenerator {

    // A(x)-x
    static Rule getSimpleRule1() {
        Term x = new Variable("x");
        ArrayList<Term> leftArgs = new ArrayList<>();
        leftArgs.add(x);
        return new Rule(new NonVariable("A", 1, leftArgs), x);
    }

    // A(x,S(y))-A(S(x),y)
    static Rule getSimpleRule2() {
        Term x = new Variable("x");
        Term y = new Variable("y");
        ArrayList<Term> leftSArgs = new ArrayList<>();
        leftSArgs.add(y);
        ArrayList<Term> rightSArgs = new ArrayList<>();
        rightSArgs.add(x);
        Term leftS = new NonVariable("S", 1, leftSArgs);
        Term rightS = new NonVariable("S", 1, rightSArgs);

        ArrayList<Term> leftAArgs = new ArrayList<>();
        leftAArgs.add(x);
        leftAArgs.add(leftS);
        ArrayList<Term> rightAArgs = new ArrayList<>();
        rightAArgs.add(rightS);
        rightAArgs.add(y);
        Term rightA = new NonVariable("A", 2, rightAArgs);
        Term leftA = new NonVariable("A", 2, leftAArgs);

        return new Rule(leftA, rightA);
    }
}
