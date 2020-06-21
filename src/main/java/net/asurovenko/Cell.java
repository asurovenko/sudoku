package net.asurovenko;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Cell {
    private int value;
    private int x;
    private int y;
    private Set<Integer> possibleValues;

    public Cell() {
        this(0, -1, -1);
    }

    public Cell(int value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.possibleValues = new HashSet<>();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
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

    public Set<Integer> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(Set<Integer> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public void setEmptyPossibleValues() {
        if (!possibleValues.isEmpty()) {
            possibleValues = new HashSet<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return value == cell.value &&
                x == cell.x &&
                y == cell.y &&
                Objects.equals(possibleValues, cell.possibleValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleValues, value, x, y);
    }
}
