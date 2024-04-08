
import java.util.Random;

public class Minefield {
    private Cell[][] field;
    private int flags;
    private int rows;
    private int columns;
    private boolean gameOver;
    private Random random;
    /**
    Global Section
    */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.field = new Cell[rows][columns];
        this.random = new Random();

        // Fill the field with default Cell objects
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.field[i][j] = new Cell(false, "-");
            }
        }

        // Set the number of flags
        this.flags = flags;

        // Game is not over at the start
        this.gameOver = false;
    }


    public void evaluateField() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // If the current cell is a mine, update adjacent cells
                if (field[i][j].getStatus().equals("M")) {
                    updateAdjacentCells(i, j);
                }
            }
        }
    }

    private void updateAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int adjRow = row + i;
                int adjCol = col + j;
                // Check if adjacent cell is within the bounds of the field
                if (adjRow >= 0 && adjRow < rows && adjCol >= 0 && adjCol < columns) {
                    // Avoid incrementing the mine itself
                    if (i != 0 || j != 0) {
                        // Increment the adjacent cell's mine count if it's not a mine
                        if (!field[adjRow][adjCol].getStatus().equals("M")) {
                            incrementCellStatus(adjRow, adjCol);
                        }
                    }
                }
            }
        }
    }
    private void incrementCellStatus(int row, int col) {
        Cell cell = field[row][col];
        String status = cell.getStatus();
        if (status.equals("-")) {
            cell.setStatus("1");
        } else {
            int count = Integer.parseInt(status);
            cell.setStatus(String.valueOf(count + 1));
        }
    }

    public void createMines(int x, int y, int mines) {
        int placedMines = 0;

        while (placedMines < mines) {
            int randomX = random.nextInt(rows);
            int randomY = random.nextInt(columns);

            // Check if the random cell is not the starting cell and not already a mine
            if ((randomX != x || randomY != y) && !field[randomX][randomY].getStatus().equals("M")) {
                field[randomX][randomY].setStatus("M");
                placedMines++;
            }
        }
    }


    public boolean guess(int x, int y, boolean flag) {
        // Check if coordinates are within the bounds of the minefield
        if (x < 0 || x >= rows || y < 0 || y >= columns) {
            return false; // Or handle out-of-bounds appropriately
        }

        Cell cell = field[x][y];

        // If flagging
        if (flag) {
            // Toggle the flag status
            if (cell.getStatus().equals("F")) {
                cell.setStatus("-");
                flags++;
            } else if (flags > 0){
                cell.setStatus("F");
                flags--;
            }
            return false; // Flagging does not end the game
        }

        // If revealing a cell
        if (!cell.getStatus().equals("F")) { // Ensure the cell isn't flagged
            cell.setRevealed(true);

            if (cell.getStatus().equals("M")) {
                // Hit a mine - game over
                gameOver = true;
                return true;
            } else if (cell.getStatus().equals("0")) {
                // Reveal surrounding zero-valued cells
                revealZeroes(x, y);
            }
        }

        return false; // Did not hit a mine
    }


    public boolean gameOver() {
        boolean allCellsRevealed = false;
        boolean mineHit = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = field[i][j];

                // Check if all non-mine cells have been revealed
                if (!cell.getRevealed() ) {
                    allCellsRevealed = false;
                    return false;
                }
            }
        }

        // Game is over if a mine was hit or if all non-mine cells are revealed
        return true;
    }


    public void revealZeroes(int x, int y) {
        Stack1Gen<CellPosition> stack = new Stack1Gen<>();
        stack.push(new CellPosition(x, y));

        while (!stack.isEmpty()) {
            CellPosition currentPos = stack.pop();
            int currentX = currentPos.getX();
            int currentY = currentPos.getY();

            // Check bounds and if the cell is already revealed
            if (currentX < 0 || currentX >= rows || currentY < 0 || currentY >= columns || field[currentX][currentY].getRevealed()) {
                continue;
            }

            Cell currentCell = field[currentX][currentY];
            currentCell.setRevealed(true);

            // If the cell is zero, push its neighbors onto the stack
            if (currentCell.getStatus().equals("0")) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i != 0 || j != 0) { // Exclude the current cell
                            int newX = currentX + i;
                            int newY = currentY + j;
                            // Making sure the new coordinates are within bounds before adding
                            if (newX >= 0 && newX < rows && newY >= 0 && newY < columns) {
                                stack.push(new CellPosition(newX, newY));

                            }
                        }
                    }
                }
            }
        }

    }

    private class CellPosition {
        private final int x;
        private final int y;

        public CellPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }


    public void revealStartingArea(int x, int y) {
        Q1Gen<int[]> s = new Q1Gen<>(); // To create queue
        s.add(new int[]{x, y});

        while (s.length() != 0) {
            int[] coordinates = s.remove();
            int newX = coordinates[0];
            int newY = coordinates[1];

            field[newX][newY].setRevealed(true); // To set status true

            if (field[newX][newY].getStatus().equals("M")) {
                break;
            }

            if (newX - 1 > -1&& !field[newX-1][newY].getRevealed()) {
                s.add(new int[]{newX - 1, newY});
            }
            if (newX + 1 < rows && !field[newX+1][newY].getRevealed()) {
                s.add(new int[]{newX + 1, newY});
            }
            if (newY - 1> -1 && !field[newX][newY-1].getRevealed()) {
                s.add(new int[]{newX, newY - 1});
            }
            if (newY + 1< columns && !field[newX][newY+1].getRevealed()) {
                s.add(new int[]{newX, newY + 1});
            }


        }
    }


    public void debug() {
        // Print column headers
        int maxDigits = Integer.toString(columns).length();

        System.out.print("  ");
        for (int i = 0; i < columns; i++) {
            System.out.printf("%" + maxDigits + "d  ", i + 1);
        }
        System.out.println(); // New line after printing column headers

        for (int i = 0; i < rows; i++) {
            // Print row headers
            System.out.printf("%" + maxDigits + "d ", i + 1); // Print row number before the row

            for (int j = 0; j < columns; j++) {
                Cell cell = field[i][j];
                String output = cell.getStatus();

                // If the cell is not a mine and not flagged, and its status is not set, assume it's zero
                if (!output.equals("M") && !output.equals("F") && output.equals("-")) {
                    output = "0";
                }

                // Apply color coding based on the status of the cell
                switch (output) {
                    case "M":
                        output = ANSI_RED_BRIGHT + output + ANSI_GREY_BACKGROUND;  // Red for mines
                        break;
                    case "F":
                        output = ANSI_BLUE_BRIGHT + output + ANSI_GREY_BACKGROUND;  // Blue for flags
                        break;
                    case "1":
                        output = ANSI_GREEN + output + ANSI_GREY_BACKGROUND;
                        break;
                    case "2":
                        output = ANSI_PURPLE + output + ANSI_GREY_BACKGROUND;
                        break;
                    case "3":
                        output = ANSI_CYAN + output + ANSI_GREY_BACKGROUND;
                        break;
                    case "-":
                        output = ANSI_CYAN + output + ANSI_GREY_BACKGROUND;
                        break;
                    case "0":
                        output = ANSI_YELLOW_BRIGHT + output + ANSI_GREY_BACKGROUND;
                        break;
                    default:
                        output = ANSI_GREEN + output + ANSI_GREY_BACKGROUND;
                        break;
                }

                System.out.print(output + "  ");
            }
            System.out.println(ANSI_RESET);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        int maxDigits = Integer.toString(columns).length();

        sb.append("\n");
        sb.append("  ");
        for (int i = 0; i < columns; i++) {
            sb.append(String.format("%" + maxDigits + "d  ", i + 1));
        }
        sb.append("\n"); // New line after printing column headers

        for (int i = 0; i < rows; i++) {
            // Print row headers
            sb.append(String.format("%" + maxDigits + "d ", i + 1)); // Print row number before the row

            for (int j = 0; j < columns; j++) {
                Cell cell = field[i][j];
                if (cell.getRevealed()) {
                    sb.append(cell.getStatus()).append("  ");
                } else {
                    // Show a placeholder for unrevealed cells
                    sb.append("-").append("  ");
                }
            }
            sb.append("\n"); // New line at the end of each row
        }

        return sb.toString();
    }
}
