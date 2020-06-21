package net.asurovenko;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

// output files made from website
public class SudokuTest {

    @Test
    public void testHard1() {
        testSudoku("hard-1-input", "hard-1-output");
    }

    @Test
    public void testHard2() {
        testSudoku("hard-2-input", "hard-2-output");
    }

    @Test
    public void testEasy1() {
        testSudoku("easy-1-input", "easy-1-output");
    }

    private void testSudoku(String inputFile, String outputFile) {
        String sudokuStr = getResource(inputFile);
        Sudoku sudoku = new Sudoku(sudokuStr);
        sudoku.solve();
        String sudokuResultFromFile = getResource(outputFile);
        assertEquals(sudoku.toString(), sudokuResultFromFile);
    }

    private String getResource(String path) {
        return new Scanner(getClass().getClassLoader()
                .getResourceAsStream(path), StandardCharsets.UTF_8)
                .useDelimiter("\\A")
                .next().replace("\r", "").trim();
    }
}
