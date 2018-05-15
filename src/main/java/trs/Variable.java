package trs;

public class Variable extends Term {

    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isVariable() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
