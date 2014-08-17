import java.util.*;
import java.util.concurrent.*;

public class SudokuSolver {
  private static final int MAX_VALUE = 9;
  private static final int[][] HARD = {
    {0, 0, 0, 0, 0, 0, 0, 3, 0},
    {0, 5, 1, 0, 6, 0, 0, 0, 2},
    {7, 0, 0, 9, 0, 8, 5, 0, 0},
    {9, 0, 0, 1, 0, 0, 0, 0, 0},
    {0, 4, 0, 0, 0, 0, 0, 8, 0},
    {0, 0, 0, 0, 0, 5, 0, 0, 3},
    {0, 0, 8, 7, 0, 2, 0, 0, 1},
    {5, 0, 0, 0, 3, 0, 0, 0, 7},
    {0, 0, 6, 0, 0, 0, 0, 0, 0},
  };
/*
Solution for Easy puzzle:
4,6,8,3,7,1,5,2,9,
5,2,9,6,4,8,7,1,3,
3,1,7,9,5,2,6,8,4,
9,3,6,1,8,7,4,5,2,
8,5,2,4,6,3,9,7,1,
1,7,4,5,2,9,8,3,6,
2,9,5,8,3,6,1,4,7,
7,4,1,2,9,5,3,6,8,
6,8,3,7,1,4,2,9,5,
*/
  private static final int[][] EASY = {
    {0, 0, 8, 0, 0, 0, 0, 0, 9},
    {0, 0, 9, 6, 4, 0, 0, 1, 0},
    {3, 0, 0, 0, 0, 0, 0, 8, 0},
    {0, 0, 6, 0, 8, 7, 0, 0, 2},
    {0, 5, 0, 4, 0, 3, 0, 7, 0},
    {1, 0, 0, 5, 2, 0, 8, 0, 0},
    {0, 9, 0, 0, 0, 0, 0, 0, 7},
    {0, 4, 0, 0, 9, 5, 3, 0, 0},
    {6, 0, 0, 0, 0, 0, 2, 0, 0},
  };

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int[][] state = new int[MAX_VALUE][MAX_VALUE];
    for (int i = 0; i < MAX_VALUE; ++i) {
      for (int j = 0; j < MAX_VALUE; ++j) {
        state[i][j] = sc.nextInt();
      }
    }

    Board board = new Board(state);
    List<CellOptions> allCellOptions = new ArrayList<CellOptions>();
    identifyCellOptions(board, allCellOptions);
    //System.out.println("Initial Options: " + allCellOptions);

    if (!solve(board, allCellOptions)) {
      System.out.println("Unsolvable puzzle");
      return;
    }
    for (int i = 0; i < board.state.length; i++) {
      for (int j = 0; j < board.state[i].length; j++)
        System.out.print(board.state[i][j] + ",");
      System.out.println();
    }
  }

  private static List<CellOptions> identifyCellOptions(Board b, List<CellOptions> allOptions) {
    for (int i = 0; i < b.state.length; i++) {
      for (int j = 0; j < b.state[i].length; j++) {
        if (b.state[i][j] != 0) {
          continue;
        }
        CellOptions currentCellOptions = new CellOptions(new Cell(i, j));
        allOptions.add(currentCellOptions);
        for (int k = 1; k <= MAX_VALUE; k++) {
          if (isAnOption(b, i, j, k)) {
            currentCellOptions.options.add(k);
          }
        }
      }
    }
    Collections.sort(allOptions);
    return allOptions;
  }

  private static boolean isAnOption(Board b, int row, int col, int value) {
    // check row
    for (int i = 0; i < b.state[row].length; i++) {
      if (i != col && b.state[row][i] == value) return false;
    }
    // check column
    for (int i = 0; i < b.state.length; i++) {
      if (i != row && b.state[i][col] == value) return false;
    }

    // check block
    int blockRow = (row / 3) * 3;
    int blockCol = (col / 3) * 3;
    for (int i = blockRow; i < blockRow + 3; ++i) {
      for (int j = blockCol; j < blockCol + 3; ++j) {
        if (b.state[i][j] == value) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean solve(Board b, List<CellOptions> options) {
    if(!b.anyFreeCell()) {
      return true;
    }
    if (options.size() == 0) return false;

    // if any cell has no valid options, short-circuit
    for (CellOptions o : options) {
      if (o.options.size() == 0) {
        return false;
      }
    }

    //System.out.println("Options: " + options);
    CellOptions cellOptions = options.remove(0);
    //System.out.println("Trying values of " + cellOptions);

    for (int value : cellOptions.options) {
      //System.out.println("Trying value: " + value + " among " + cellOptions);
      makeMove(b, options, cellOptions, value);
      if (solve(b, options))
        return true;
      //System.out.println("back-tracking value: " + value + " among " + cellOptions);
      undoMove(b, options, cellOptions, value);
    }

    // without doing this, back-tracking will not add back the "value" on cellOptions that were removed
    options.add(0, cellOptions);
    return false;
  }

  private static void makeMove(Board b, List<CellOptions> allCellOptions, CellOptions options, int value) {
    b.state[options.c.row][options.c.col] = value;
    // Remove value from cellOptions of cells in the same row, col and block
    int block = (options.c.row / 3) * 3 + (options.c.col / 3);
    for (CellOptions cellOptions : allCellOptions) {
      int otherBlock = (cellOptions.c.row / 3) * 3 + (cellOptions.c.col / 3);
      if (cellOptions.c.row == options.c.row || cellOptions.c.col == options.c.col ||
          otherBlock == block) {
        if (cellOptions.options.remove(value)) {
          //System.out.println("Removed value: " + value + " from " + cellOptions + " after changing " + options);
        }
      }
    }
    // With the updates above, ensure we pick the next cell with the least choices
    Collections.sort(allCellOptions);
  }

  private static void undoMove(Board b, List<CellOptions> allCellOptions, CellOptions options, int value) {
    b.state[options.c.row][options.c.col] = 0;
    // insert value into cellOptions of cells in the same row, col and block if it is valid
    int block = (options.c.row / 3) * 3 + (options.c.col / 3);
    for (CellOptions cellOptions : allCellOptions) {
      int otherBlock = (cellOptions.c.row / 3) * 3 + (cellOptions.c.col / 3);
      if (cellOptions.c.row == options.c.row || cellOptions.c.col == options.c.col ||
          otherBlock == block) {
        if (isAnOption(b, cellOptions.c.row, cellOptions.c.col, value)) {
          cellOptions.options.add(value);
          //System.out.println("Added back value: " + value + " to " + cellOptions + " after undoing " + options);
        }
      }
    }
    // Sorting the options will change the state of the back-tracking
    //Collections.sort(allCellOptions);
  }
}

class Cell {
  int row;
  int col;

  public Cell(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public int hashCode() {
    return row * 13 + col;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null || !(o instanceof Cell)) return false;
    Cell other = (Cell) o;
    return this.row == other.row && this.col == other.col;
  }
}

class CellOptions implements Comparable {
  Cell c;
  Set<Integer> options = new HashSet<Integer>();

  public CellOptions(Cell c) {
    this.c = c;
  }

  public int hashCode() {
    return options.size() * 37 + c.row * 29 + c.col * 13;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (o == null || !(o instanceof CellOptions)) return false;

    CellOptions other = (CellOptions) o;
    return this.options.equals(other.options) && this.c.equals(other.c);
  }

  public int compareTo(Object o) {
    if (!(o instanceof CellOptions)) return -1;
    CellOptions other = (CellOptions) o;
    return (this.options.size() - other.options.size()) * 100 +
      (this.c.row - other.c.row) * 10 +
      (this.c.col - other.c.col);
  }

  public String toString() {
    return "Options for cell: [" + c.row + ", " + c.col + "] is: " + options;
  }
}

class Board {
  private static final int N = 9;
  int[][] state = new int[N][N];
  Board(int[][] state) {
    this.state = state;
  }

  public boolean anyFreeCell() {
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++) 
        if (state[i][j] == 0) return true;
    return false;
  }
}
