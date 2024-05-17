package foop.world;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ConnectedSubwaysPlayingField extends JPanel {
    private final int[][] grid;
    private final int[][] tempGrid;
    private final int numRows;
    private final int numCols;
    private final int numSubways;
    private final int type;

    public ConnectedSubwaysPlayingField(long seed1, int type, int numRows, int numCols, int numbSubways) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.numSubways = numbSubways;
        this.grid = new int[numRows + 1][numCols + 1];
        this.tempGrid = new int[numRows + 1][numCols + 1];
        this.type = type;
        // Initialize the grid with zeros
        for (int i = 0; i < numRows + 1; i++) {
            for (int j = 0; j < numCols + 1; j++) {
                grid[i][j] = 0;
                tempGrid[i][j] = 0;
            }
        }

        placeConnectedSubways(seed1);
    }

    private void placeConnectedSubways(long seed1) {
        List<Point> availableCells = new ArrayList<>();
        for (int i = 1; i < numRows; i++) {
            for (int j = 1; j < numCols; j++) {
                availableCells.add(new Point(i, j));
            }
        }

        Collections.shuffle(availableCells, new Random(seed1));

        for (int i = 1; i <= numSubways; i++) { //start with 1, since 0 is used for empty cells
            if (availableCells.isEmpty()) {
                break; // No more available cells
            }

            List<Point> subwayCells = new LinkedList<Point>();

            Point cell1 = availableCells.removeFirst();
            subwayCells.add(cell1);
            tempGrid[cell1.x][cell1.y] = i;
            Point currCell = cell1;
            int numSubwayCells = 0;
            while (currCell != null && numSubwayCells < 8) {
                Point newCell = getNeighbor(seed1, availableCells, currCell, i);
                if (newCell != null) {
                    tempGrid[newCell.x][newCell.y] = i;
                    subwayCells.add(newCell);
                }
                currCell = newCell;
                numSubwayCells++;
            }

            //subway has to be at least 4 cells long
            if (subwayCells.size() > 3) {
                for (int j = 0; j < subwayCells.size(); j++) {
                    Point cell = subwayCells.get(j);
                    if (j == 0 || j == subwayCells.size() - 1) grid[cell.x][cell.y] = -i;
                    else grid[cell.x][cell.y] = i;
                }
            }
        }
    }

    private Point getNeighbor(long seed1, List<Point> availableCells, Point cell, int color) {

        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(cell.x - 1, cell.y)); // Up
        neighbors.add(new Point(cell.x + 1, cell.y)); // Down
        neighbors.add(new Point(cell.x, cell.y - 1)); // Left
        neighbors.add(new Point(cell.x, cell.y + 1)); // Right

        Collections.shuffle(neighbors, new Random(seed1)); // Randomize the order of neighbors

        for (Point neighbor : neighbors) {
            if (availableCells.contains(neighbor) && isWithinGrid(neighbor) && noUturn(neighbor, color)) {
                availableCells.remove(neighbor); // Remove the cell from available cells
                return neighbor;

            }
        }

        return null; // No available neighbor found within the distance limit
    }

    private boolean noUturn(Point cell, int color) {
        int count = 0;

        //check in all 4 directions whether a cell of the same color is a neighbor
        if (tempGrid[cell.x - 1][cell.y] == color) {
            count += 1;
        }
        if (tempGrid[cell.x + 1][cell.y] == color) {
            count += 1;
        }
        if (tempGrid[cell.x][cell.y + 1] == color) {
            count += 1;
        }
        if (tempGrid[cell.x][cell.y - 1] == color) {
            count += 1;
        }

        return count <= 1;
    }

    private boolean isWithinGrid(Point cell) {
        return cell.x < 10 && cell.y < 10;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellWidth = getWidth() / numCols;
        int cellHeight = getHeight() / numRows;
        List<Color> colors = new LinkedList<>();
        colors.add(Color.red);
        colors.add(Color.green);
        colors.add(Color.blue);
        colors.add(Color.yellow);

        // Draw grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (this.type == 0) {
                    if (grid[i][j] == 0) {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        g.setColor(Color.BLACK);
                        g.drawRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    } else if (grid[i][j] < 0) {
                        g.setColor(colors.get((grid[i][j] * -1) % 4));
                        g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    } else {
                        g.setColor(colors.get(grid[i][j] % 4).darker());
                        g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                } else if (type == 1) {
                    if (grid[i][j] < 0) {
                        g.setColor(colors.get((grid[i][j] * -1) % 4));
                        g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        g.setColor(Color.BLACK);
                        g.drawRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int numRows = 10;
        int numCols = 10;
        int numSubways = 3; // Configure the maximum number of subways
        long seed1 = new Random().nextLong();

        JFrame frameSubway = new JFrame("Cats and Mice in the Subway: Subway View");
        int type0 = 0;
        ConnectedSubwaysPlayingField playingField = new ConnectedSubwaysPlayingField(seed1, type0, numRows, numCols, numSubways);
        frameSubway.add(playingField);
        frameSubway.setSize(500, 500);
        frameSubway.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameSubway.setVisible(true);

        JFrame frameTop = new JFrame("Cats and Mice in the Subway: Top View");
        int type1 = 1;
        ConnectedSubwaysPlayingField playingField2 = new ConnectedSubwaysPlayingField(seed1, type1, numRows, numCols, numSubways);
        frameTop.add(playingField2);
        frameTop.setSize(500, 500);
        frameTop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameTop.setVisible(true);
    }
}
