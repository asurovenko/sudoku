# sudoku

### Important note
It may be not fully correct algorithm, it was checked using several "expert level" sudoku. Wrong solutions were not found, but it doesnt mean that everyone else is correct. If you notice any wrong solutions, please let me know.
This is just a pet project and because of that the architecture not the best. The project requires refactoring for better readability of the code(if it is needed, let me know).

This is just pet project and because of that the architecture not the best. 
The project requires refactoring for better readability of the code(if it will be needed, let me know).

## Algorithm
For all cells calculated possible values based on common sudoku rules and if there's single possible value - just set it.
After that based on possible values checked every block and if in the block there's the value wich present only in single line or single column then we can remove that possible value from other blocks in that line or column.
With help of that to find the cell with only one possible value and we can set it.