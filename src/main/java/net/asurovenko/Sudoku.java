package net.asurovenko;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class Sudoku {

    private static final Set<Integer> ALL_NUMBERS = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
    private static final int NUMBER_OFFSET = 48;

    private final Cell[][] matrix;
    private final Set<Integer>[] linesNumbers = new HashSet[9];
    private final Set<Integer>[] columnsNumbers = new HashSet[9];
    private final Set<Integer>[][] blocksNumbers = new HashSet[3][3];

    public Sudoku(String str) {
        matrix = new Cell[9][9];
        init();
        String[] rows = str.replace("\r", "").replace("_", "0").split("\n");
        if (rows.length != 9) {
            throw new IllegalArgumentException("Wrong string value. Rows number must be 9");
        }
        for (int i = 0; i < 9; i++) {
            if (rows[i].length() != 9) {
                throw new IllegalArgumentException("Wrong string value. Column number must be 9");
            }
            char[] digits = rows[i].toCharArray();
            for (int j = 0; j < digits.length; j++) {
                int value = digits[j] - NUMBER_OFFSET;
                matrix[i][j] = new Cell();
                matrix[i][j].setValue(value);
                matrix[i][j].setX(i);
                matrix[i][j].setY(j);
                if (value != 0) {
                    set(i, j, value);
                }
            }
        }
    }

    public void solve() {
        recalculatePossibleValues();
        do {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (checkCell(i, j)) {
                        recalculatePossibleValues();
                    }
                }
            }
        } while (hasEmpty());
    }

    private void init() {
        for (int i = 0; i < 9; i++) {
            linesNumbers[i] = new HashSet<>();
            columnsNumbers[i] = new HashSet<>();
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                blocksNumbers[i][j] = new HashSet<>();
            }
        }
    }

    private void set(int x, int y, int value) {
        linesNumbers[x].add(value);
        columnsNumbers[y].add(value);
        blocksNumbers[y / 3][x / 3].add(value);
        matrix[x][y].setValue(value);
    }

    private void recalculatePossibleValues() {
        while (recalculatePossibleValue()) ;
    }

    //return true if new value is set
    private boolean recalculatePossibleValue() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = matrix[i][j];
                if (cell.getValue() == 0) {
                    Set<Integer> possibleValues = calculatePossibleValues(i, j);
                    if (possibleValues.size() == 1) {
                        set(i, j, possibleValues.iterator().next());
                        return true;
                    }
                    cell.setPossibleValues(possibleValues);
                } else {
                    cell.setEmptyPossibleValues();
                }
            }
        }
        return checkAllBlocksByPossibleValue();
    }

    //return true if new value is set
    private boolean checkAllBlocksByPossibleValue() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (checkBlockByPossibleValue(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkBlockByPossibleValue(int x, int y) {
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
        for (Map.Entry<Integer, Set<Pair<Integer, Integer>>> entry : map.entrySet()) {
            if (entry.getValue().stream().map(Pair::getLeft).distinct().count() == 1) {
                Integer line = entry.getValue().iterator().next().getLeft();
                for (int i = 0; i < 9; i++) {
                    if (i < y * 3 || i >= (y * 3) + 3) {
                        Set<Integer> possibleValues = matrix[line][i].getPossibleValues();
                        possibleValues.remove(entry.getKey());
                        if (possibleValues.size() == 1) {
                            set(line, i, possibleValues.iterator().next());
                            return true;
                        }
                    }
                }
            }
            if (entry.getValue().stream().map(Pair::getRight).distinct().count() == 1) {
                Integer col = entry.getValue().iterator().next().getRight();
                for (int i = 0; i < 9; i++) {
                    if (i < x * 3 || i >= (x * 3) + 3) {
                        Set<Integer> possibleValues = matrix[i][col].getPossibleValues();
                        possibleValues.remove(entry.getKey());
                        if (possibleValues.size() == 1) {
                            set(i, col, possibleValues.iterator().next());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

        int fromX = x - (x % 3);
        int toX = fromX + 3;

        int fromY = y - (y % 3);
        int toY = fromY + 3;

        for (int i = fromX; i < toX; i++) {
            for (int j = fromY; j < toY; j++) {
                Set<Integer> possibleValues = matrix[i][j].getPossibleValues();
                for (Integer v : possibleValues) {
                    map.put(v, map.getOrDefault(v, 0) + 1);
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                for (int i = fromX; i < toX; i++) {
                    for (int j = fromY; j < toY; j++) {
                        Set<Integer> possibleValues = matrix[i][j].getPossibleValues();
                        if (possibleValues.contains(entry.getValue())) {
                            set(i, j, entry.getValue());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //return true if new value is set
    private boolean checkCellByLine(int x) {
        //number -> count in line
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            Set<Integer> possibleValues = matrix[x][i].getPossibleValues();
            for (Integer v : possibleValues) {
                map.put(v, map.getOrDefault(v, 0) + 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                Integer nuber = entry.getKey();
                for (int i = 0; i < 9; i++) {
                    if (tryToUpdate(x, i, nuber)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //return true if new value is set
    private boolean checkCellByCol(int y) {
        //number -> count in col
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            Set<Integer> possibleValues = matrix[i][y].getPossibleValues();
            for (Integer v : possibleValues) {
                map.put(v, map.getOrDefault(v, 0) + 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                Integer nuber = entry.getKey();
                for (int i = 0; i < 9; i++) {
                    if (tryToUpdate(i, y, nuber)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean tryToUpdate(int x, int y, int value) {
        Set<Integer> possibleValues = matrix[x][y].getPossibleValues();
        if (possibleValues.contains(value)) {
            set(x, y, value);
            return true;
        }
        return false;
    }

    private Set<Integer> calculatePossibleValues(int x, int y) {
        Set<Integer> set = new HashSet<>(ALL_NUMBERS);
        set.removeAll(linesNumbers[x]);
        set.removeAll(columnsNumbers[y]);
        set.removeAll(blocksNumbers[y / 3][x / 3]);
        return set;
    }

    private boolean hasEmpty() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (matrix[i][j].getValue() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(90);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = matrix[i][j];
                if (cell.getValue() != 0) {
                    sb.append(cell.getValue());
                } else {
                    sb.append("_");
                }
            }
            sb.append('\n');
        }
        return sb.toString().trim();
    }

}
