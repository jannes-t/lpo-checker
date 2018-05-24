package trs;

public class Rule {

    private Term left;
    private Term right;

    public Rule(Term left, Term right) {
        this.left = left;
        this.right = right;
    }

    public Term getLeft() {
        return left;
    }

    public Term getRight() {
        return right;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", left.toString(), right.toString());
    }
}
