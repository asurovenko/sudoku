package net.asurovenko;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewSudoku {

    private final Cell[][] matrix;
    private final Set<Integer>[] lines = new HashSet[9];
    private final Set<Integer>[] columns = new HashSet[9];
    private final Set<Integer>[][] blocks = new HashSet[3][3];

    public NewSudoku(Cell[][] matrix) {
        this.matrix = matrix;
        for (int i = 0; i < 9; i++) {
            lines[i] = new HashSet<>();
            columns[i] = new HashSet<>();
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                blocks[i][j] = new HashSet<>();
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = matrix[i][j].getValue();
                if (value != 0) {
                    set(i, j, value);
                }
            }
        }
    }

    public NewSudoku(String str) {
        this(fillMatrixFromStr(str));
    }

    private void set(int x, int y, int value) {
        lines[x].add(value);
        columns[y].add(value);
        blocks[y / 3][x / 3].add(value);
        matrix[x][y].setValue(value);
    }

    private void setAndRecalculate(int x, int y, int value) {
        set(x, y, value);
        recalculatePossibleValues();
    }

    private static Cell[][] fillMatrixFromStr(String str) {
        Cell[][] result = new Cell[9][9];
        String[] rows = str.replace("\r", "").replace("_", "0").split("\n");
        if (rows.length != 9) {
            throw new IllegalArgumentException("Wrong string value. Rows number must be 9");
        }
        for (int i = 0; i < rows.length; i++) {
            if (rows[i].length() != 9) {
                throw new IllegalArgumentException("Wrong string value. Column number must be 9");
            }
            char[] digits = rows[i].toCharArray();
            for (int j = 0; j < digits.length; j++) {
                result[i][j] = new Cell();
                result[i][j].setValue(digits[j] - 48);
                result[i][j].setX(i);
                result[i][j].setY(j);
            }
        }
        return result;
    }

    private void recalculatePossibleValues() {
        while (recalculatePossibleValue()) ;
    }

    //return true if new value is set
    private boolean recalculatePossibleValue() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = this.matrix[i][j];
                if (cell.getValue() == 0) {
                    Set<Integer> possibleValues = calculatePossibleValues(i, j);
                    if (possibleValues.size() == 1) {
                        set(i, j, possibleValues.iterator().next());
                        return true;
                    }
                    cell.setPossibleValues(possibleValues);
                } else {
                    cell.setPossibleValues(new HashSet<>());
                }
            }
        }
        return checkAllBlocks();
    }

    private boolean checkAllBlocks() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (checkBlock(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkBlock(int x, int y) {
        Map<Integer, Set<Pair<Integer, Integer>>> map = new HashMap<>();
        for (int i = x * 3; i < (x * 3) + 3; i++) {
            for (int j = y * 3; j < (y * 3) + 3; j++) {
                for (Integer value : matrix[i][j].getPossibleValues()) {
                    Set<Pair<Integer, Integer>> currentValues = map.getOrDefault(value, new HashSet<>());
                    currentValues.add(Pair.of(i, j));
                    map.put(value, currentValues);
                }
            }
        }
        AtomicBoolean result = new AtomicBoolean(false);
        map.forEach((value, pairs) -> {
            if (pairs.stream().map(Pair::getLeft).distinct().count() == 1) {
                Integer line = pairs.iterator().next().getLeft();
                for (int i = 0; i < 9; i++) {
                    if (i < y * 3 || i >= (y * 3) + 3) {
                        Set<Integer> possibleValues = matrix[line][i].getPossibleValues();
                        possibleValues.remove(value);
                        if (possibleValues.size() == 1) {
                            set(line, i, possibleValues.iterator().next());
                            result.set(true);
                            return;
                        }
                    }
                }
            }
            if (pairs.stream().map(Pair::getRight).distinct().count() == 1) {
                Integer col = pairs.iterator().next().getRight();
                for (int i = 0; i < 9; i++) {
                    if (i < x * 3 || i >= (x * 3) + 3) {
                        Set<Integer> possibleValues = matrix[i][col].getPossibleValues();
                        possibleValues.remove(value);
                        if (possibleValues.size() == 1) {
                            set(i, col, possibleValues.iterator().next());
                            result.set(true);
                            return;
                        }
                    }
                }
            }
        });
        return result.get();
    }

    //return true if new value is set
    private boolean checkCell(int x, int y) {
        if (matrix[x][y].getValue() != 0) {
            return false;
        }
        if (checkCellByLine(x)) {
            return true;
        }
        if (checkCellByCol(y)) {
            return true;
        }
        if (checkCellByBlock(x, y)) {
            return true;
        }

        return false;
    }

    //return true if new value is set
    private boolean checkCellByBlock(int x, int y) {
        Map<Integer, Integer> map = new HashMap<>();

        int fromX = x - (x%3);
        int toX = fromX + 3;

        int fromY = y - (y%3);
        int toY = fromY + 3;

        for (int i = fromX; i < toX; i++) {
            for (int j = fromY; j < toY; j++) {
                Set<Integer> possibleValues = matrix[i][j].getPossibleValues();
                for (Integer v : possibleValues) {
                    map.put(v, map.getOrDefault(v, 0) + 1);
                }
            }
        }

        AtomicBoolean isUpdated = new AtomicBoolean(false);
        map.forEach((v, count) -> {
            if (count == 1) {
                for (int i = fromX; i < toX; i++) {
                    for (int j = fromY; j < toY; j++) {
                        Set<Integer> possibleValues = matrix[i][j].getPossibleValues();
                        if (possibleValues.contains(v)) {
                            set(i, j, v);
                            isUpdated.set(true);
                            return;
                        }
                    }
                }
            }
        });
        return isUpdated.get();
    }

    //return true if new value is set
    private boolean checkCellByLine(int x) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            Set<Integer> possibleValues = matrix[x][i].getPossibleValues();

            for (Integer v : possibleValues) {
                map.put(v, map.getOrDefault(v, 0) + 1);
            }
        }
        AtomicBoolean isUpdated = new AtomicBoolean(false);
        map.forEach((v, count) -> {
            if (count == 1) {
                for (int i = 0; i < 9; i++) {
                    Set<Integer> possibleValues = matrix[x][i].getPossibleValues();
                    if (possibleValues.contains(v)) {
                        set(x, i, v);
                        isUpdated.set(true);
                        return;
                    }
                }
            }
        });
        return isUpdated.get();
    }

    //return true if new value is set
    private boolean checkCellByCol(int y) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            Set<Integer> possibleValues = matrix[i][y].getPossibleValues();

            for (Integer v : possibleValues) {
                map.put(v, map.getOrDefault(v, 0) + 1);
            }
        }
        AtomicBoolean isUpdated = new AtomicBoolean(false);
        map.forEach((v, count) -> {
            if (count == 1) {
                for (int i = 0; i < 9; i++) {
                    Set<Integer> possibleValues = matrix[i][y].getPossibleValues();
                    if (possibleValues.contains(v)) {
                        set(i, y, v);
                        isUpdated.set(true);
                        return;
                    }
                }
            }
        });
        return isUpdated.get();
    }

    private Set<Integer> calculatePossibleValues(int x, int y) {
        Set<Integer> set = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            set.add(i);
        }
        set.removeAll(lines[x]);
        set.removeAll(columns[y]);
        set.removeAll(blocks[y / 3][x / 3]);
        return set;
    }

    public static void main(String[] args) throws IOException {
        String s = Files.readString(Path.of("C:\\Users\\alexey\\Desktop\\8.txt"));
        NewSudoku sudoku = new NewSudoku(s);
        sudoku.recalculatePossibleValues();

        for (int k = 0; k < 1000; k++) {


            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (sudoku.checkCell(i, j)) {
                        sudoku.recalculatePossibleValues();
                    }
                }
            }

        }
        System.out.println();
        sudoku.print();

    }

    private void print() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = matrix[i][j];
                if (cell.getValue() != 0) {
                    System.out.print(cell.getValue());
                } else {
                    System.out.print("_");
                }
            }
            System.out.println();
        }
    }
}
