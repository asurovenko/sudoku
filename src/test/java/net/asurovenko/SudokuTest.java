package net.asurovenko;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class SudokuTest {

    @Test
    public void testHard1() {
        testSudoku("hard-1-input.txt", "hard-1-output.txt");
    }

    @Test
    public void testEasy1() {
        testSudoku("easy-1-input.txt", "easy-1-output.txt");
    }

    private void testSudoku(String inputFile, String outputFile) {
        String sudokuStr = getResource(inputFile);
        Sudoku sudoku = new Sudoku(sudokuStr);
        sudoku.solve();
        String result = sudoku.print();
        String sudokuResultFromFile = getResource(outputFile);
        assertEquals(result, sudokuResultFromFile);
    }

    private String getResource(String path) {
        return new Scanner(getClass().getClassLoader()
                .getResourceAsStream(path), StandardCharsets.UTF_8)
                .useDelimiter("\\A")
                .next().replace("\r", "").trim();
    }
}
