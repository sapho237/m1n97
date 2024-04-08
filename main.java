
import java.util.Random;
import java.util.Scanner;

public class main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean debugMode = false;

        System.out.println("Select game mode: '1' for Easy, '2' for Medium, '3' for Hard");
        System.out.println("1 - Easy (5x5, 5 mines)");
        System.out.println("2 - Medium (9x9, 12 mines)");
        System.out.println("3 - Hard (20x20, 40 mines)");
        int gameMode = scanner.nextInt();

        // Set game parameters based on the selected mode
        int rows = 0, columns = 0, mines = 0, flags = 0;
        switch (gameMode) {
            case 1:
                rows = columns = mines = flags = 5;
                break;
            case 2:
                rows = columns = 9;
                mines = flags = 12;
                break;
            case 3:
                rows = columns = 20;
                mines = flags = 40;
                break;
            default:
                System.out.println("Invalid mode selected. Exiting.");
                return;
        }

        while (true) {
            System.out.println("Enter '1' for debug mode, '0' for normal mode:");
            int debugInput = scanner.nextInt();
            if (debugInput == 0 || debugInput == 1) {
                debugMode = (debugInput == 1);
                break;
            } else {
                System.out.println("Invalid input. Please enter '1' or '0'.");
            }
        }

        Minefield minefield = new Minefield(rows, columns, mines);
        System.out.println("Enter starting x and y pos with space:");
        String f = scanner.nextLine();
        String[] coords = scanner.nextLine().split(" ");
        int xC = Integer.parseInt(coords[0]);
        int yC = Integer.parseInt(coords[1]);
        minefield.createMines(xC, yC, mines);
        minefield.evaluateField();
        minefield.revealStartingArea(xC, yC);
        // Game loop
        while (!minefield.gameOver()) {
            if (debugMode) {
                minefield.debug();
            }
                System.out.println(minefield.toString());


            // Get user guess
            System.out.println("Enter your move in the format: row column action with space");
            System.out.println("Row and column should be numbers. Action should be '1' to place/remove a flag, or '0' to reveal the cell.");
            System.out.println("For example, '3 4 1' will place/remove a flag on the cell at row 3, column 4.");
            System.out.println("Enter your move:");
            int x, y, action;
            while (true) {
                x = scanner.nextInt() - 1;
                y = scanner.nextInt() - 1;
                action = scanner.nextInt();
                if (x >= 0 && x < rows && y >= 0 && y < columns && (action == 0 || action == 1)) {
                    break;
                } else {
                    System.out.println("Invalid move. Rows and columns must fit the board, and action must be 0 (reveal) or 1 (flag).");
                }
            }

            boolean mineHit = minefield.guess(x, y, action == 1);
            if (mineHit) {
                System.out.println("You hit a mine! Game over.");
                break;
            }

            if (minefield.gameOver()) {
                System.out.println("Congratulations! You have cleared the minefield.");
                break;
            }
        }

        scanner.close();
    }
}


