package battleship;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Ship[] ships = new Ship[5];
        String[] names = new String[]{"Aircraft Carrier", "Battleship", "Submarine", "Cruiser", "Destroyer"};
        int[] lengths = new int[]{5, 4, 3, 3, 2};
        for (int p = 0; p < 2; p++) {
            System.out.println("Player " + (p + 1) + ", place your ships on the game field");
            Ship.printOcean(0, p);
            for (int i = 0; i < ships.length; i++) {
                System.out.print("Enter the coordinates of the " + names[i] + " (" + lengths[i] + " cells): ");
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                try {
                    ships[i] = new Ship(new Coordinate(parts[0].charAt(0), Integer.parseInt(parts[0].substring(1))), new Coordinate(parts[1].charAt(0), Integer.parseInt(parts[1].substring(1))), lengths[i], names[i], i + 1, p);
                    ships[i].printOcean(0, p);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    i--;
                }
            }
            Ship.unSunkShips[p] = 5;
            clearScreen(scanner);
        }

        while (Ship.unSunkShips[0] != 0 && Ship.unSunkShips[1] != 0) {
            for (int p = 0; p < 2; p++) {
                Ship.printOcean(6, 1 - p);
                System.out.println("---------------------");
                Ship.printOcean(0, p);
                System.out.println("Player " + (p + 1) + ", it's your turn:");
                takeShot(scanner, 1 - p);
            }
        }
    }

    public static void takeShot(Scanner scanner, int p) {
        String guess = scanner.nextLine();
        try {
            boolean isHit = Ship.checkGuessHit(new Coordinate(guess.charAt(0), Integer.parseInt(guess.substring(1))),  p);
            int count = Ship.getUnsunkShipCount(p);
            if (count != Ship.unSunkShips[p]) {
                System.out.println(count != 0 ? "You sank a ship! Specify a new target:" : "You sank the last ship. You won. Congratulations!");
                Ship.unSunkShips[p]--;
            } else {
                System.out.println(isHit ? "You hit a ship!" : "You missed.");
            }
            clearScreen(scanner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            takeShot(scanner, p);
        }
    }

    public static void clearScreen(Scanner scanner) {
        System.out.print("Press Enter and pass the move to another player");
        scanner.nextLine();
        System.out.println("...");
    }
}

class Ship {
    private final int length;
    private final Coordinate startCoordinate;
    private final Coordinate endCoordinate;
    private static int[][][] ocean = new int[2][10][10];

    public static int[] unSunkShips = new int[2];

    Ship(Coordinate startCoordinate, Coordinate endCoordinate, int length, String name, int shipId, int p) throws Exception {
        this.length = length;
        if (startCoordinate.getIndexX() != endCoordinate.getIndexX() && startCoordinate.getIndexY() != endCoordinate.getIndexY()) {
            throw new Exception("Error! Wrong ship location! Try again:");
        }
        if (length != Math.abs(endCoordinate.getIndexX() - startCoordinate.getIndexX()) + 1 && length != Math.abs(endCoordinate.getIndexY() - startCoordinate.getIndexY()) + 1) {
            throw new Exception("Error! Wrong length of the " + name + "! Try again:");
        }
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        isOverlapping(shipId, p);
    }

    public static boolean checkGuessHit(Coordinate c, int p) {
        if (ocean[p][c.getIndexY()][c.getIndexX()] >= 1) {
            ocean[p][c.getIndexY()][c.getIndexX()] = 7;
            return true;
        } else {
            ocean[p][c.getIndexY()][c.getIndexX()] = 8;
            return false;
        }
    }

    public static int getUnsunkShipCount(int p) {
        ArrayList<Integer> ships = new ArrayList<Integer>(5);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (ocean[p][i][j] > 0 && ocean[p][i][j] < 6 && !ships.contains(ocean[p][i][j])) {
                    ships.add(ocean[p][i][j]);
                }
            }
        }
        return ships.size();
    }

    void isOverlapping(int shipId, int p) throws Exception {
        for (int i = 0; i < length; i++) {
            if (startCoordinate.getIndexX() == endCoordinate.getIndexX()) {
                if (0 != ocean[p][Math.min(startCoordinate.getIndexY(), endCoordinate.getIndexY()) + i][startCoordinate.getIndexX()]) {
                    throw new Exception("Error! You placed it too close to another one. Try again:");
                }
            } else {
                if (0 != ocean[p][startCoordinate.getIndexY()][Math.min(startCoordinate.getIndexX(), endCoordinate.getIndexX()) + i]) {
                    throw new Exception("Error! You placed it too close to another one. Try again:");
                }
            }
        }
        if (startCoordinate.getIndexX() == endCoordinate.getIndexX()) {
            for (int i = 0; i < length; i++) {
                for (int j = -1; j <= 1; j++) {
                    int temp = startCoordinate.getIndexX() + j;
                    if (j == 0) {
                        ocean[p][Math.min(startCoordinate.getIndexY(), endCoordinate.getIndexY()) + i][temp] = shipId;
                    } else if (temp >= 0 && temp < 10) {
                        ocean[p][Math.min(startCoordinate.getIndexY(), endCoordinate.getIndexY()) + i][temp] = -1;
                    }
                }
            }
            try {
                ocean[p][Math.min(startCoordinate.getIndexY(), endCoordinate.getIndexY()) - 1][startCoordinate.getIndexX()] = -1;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                ocean[p][Math.max(startCoordinate.getIndexY(), endCoordinate.getIndexY()) + 1][startCoordinate.getIndexX()] = -1;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        } else {
            for (int i = 0; i < length; i++) {
                for (int j = -1; j <= 1; j++) {
                    int temp = startCoordinate.getIndexY() + j;
                    if (j == 0) {
                        ocean[p][temp][Math.min(startCoordinate.getIndexX(), endCoordinate.getIndexX()) + i] = shipId;
                    } else if (temp >= 0 && temp < 10) {
                        ocean[p][temp][Math.min(startCoordinate.getIndexX(), endCoordinate.getIndexX()) + i] = -1;
                    }
                }
            }
            try {
                ocean[p][startCoordinate.getIndexY()][Math.min(startCoordinate.getIndexX(), endCoordinate.getIndexX()) - 1] = -1;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                ocean[p][startCoordinate.getIndexY()][Math.max(startCoordinate.getIndexX(), endCoordinate.getIndexX()) + 1] = -1;
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    public static void printOcean(int boundary, int p) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < 10; i++) {
            System.out.print((char) (i + 'A') + " ");
            for (int j = 0; j < 10; j++) {
                System.out.print(ocean[p][i][j] <= boundary ? "~ " : ocean[p][i][j] == 7 ? "X " : ocean[p][i][j] == 8 ? "M " : "O ");
            }
            System.out.println();
        }
    }
}

class Coordinate {
    private char y;
    private int x;

    Coordinate(char c, int i) throws Exception {
        if ('A' > c || 'J' < c || 1 > i || 10 < i) {
            throw new Exception("Error! You entered the wrong coordinates! Try again: ");
        }
        y = c;
        x = i;
    }

    public int getIndexY() {
        return y - 'A';
    }

    public int getIndexX() {
        return x - 1;
    }
}
