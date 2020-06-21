package net.asurovenko;

import java.util.Set;

public class PossibleValuesModel {
    private int x;
    private int y;
    private Set<Integer> values;

    public PossibleValuesModel(int x, int y, Set<Integer> values) {
        this.x = x;
        this.y = y;
        this.values = values;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Set<Integer> getValues() {
        return values;
    }

    public void setValues(Set<Integer> values) {
        this.values = values;
    }
}
