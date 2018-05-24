package trs;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return name.equals(variable.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
