# sudoku

### Important note
It can be not fully right algorithm, it checked used several "expert level" sudoku. 
Wrong solutions were not found, but it cannot mean that everyone else will be correct. If you notice any wrong solution, please let me know.

This is just pet project and because of that the architecture not the best. 
The project requires refactoring for better readability of the code(if it will be needed, let me know).

## Algorithm
For all cells calculated possible values based on common sudoku rules and if there's single possible value - just set it.
After that based on possible values checked every block and if in the block there's the value wich present only in single line or single column then we can remove that possible value from other blocks in that line or column.
With help of that to find the cell with only one possible value and we can set it.