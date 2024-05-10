package foop.world;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectedSubwaysPlayingField extends JPanel {
    private int[][] grid;
    private int numRows;
    private int numCols;
    private int numPairs;

    public ConnectedSubwaysPlayingField(int numRows, int numCols, int numPairs) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.numPairs = numPairs;
        this.grid = new int[numRows][numCols];
        // Initialize the grid with zeros
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = 0;
            }
        }

        placeConnectedSubways();
    }

    private void placeConnectedSubways() {
        List<Point> availableCells = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                availableCells.add(new Point(i, j));
            }
        }

        Collections.shuffle(availableCells);

        for (int i = 0; i < numPairs; i++) {
            if (availableCells.isEmpty()) {
                break; // No more available cells
            }

            //three points to guarantee that subways is at least three cells long
            Point cell1 = availableCells.remove(0);
            Point cell2 = getNeighbor(availableCells, cell1);
            Point cell3 = getNeighbor(availableCells, cell2);

            if (cell2 != null && cell3 != null) {
                grid[cell1.x][cell1.y] = 1;
                grid[cell2.x][cell2.y] = 1;
                grid[cell3.x][cell3.y] = 1;
            }
        }
    }

    private Point getNeighbor(List<Point> availableCells, Point cell) {
        int maxDistance = Math.min(numRows, numCols) - 4; // Maximum distance between the first and last cell

        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(cell.x - 1, cell.y)); // Up
        neighbors.add(new Point(cell.x + 1, cell.y)); // Down
        neighbors.add(new Point(cell.x, cell.y - 1)); // Left
        neighbors.add(new Point(cell.x, cell.y + 1)); // Right

        Collections.shuffle(neighbors); // Randomize the order of neighbors

        for (Point neighbor : neighbors) {
            if (availableCells.contains(neighbor) && isWithinGrid(neighbor)) {
                availableCells.remove(neighbor); // Remove the cell from available cells
                if (isWithinGrid(neighbor)) {
                    return neighbor;
                }
            }
        }

        return null; // No available neighbor found within the distance limit
    }
    private boolean isWithinGrid(Point cell){
        if (cell.x < 10 && cell.y < 10) return true;
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellWidth = getWidth() / numCols;
        int cellHeight = getHeight() / numRows;

        // Draw grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(Color.BLACK);
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

    public static void main(String[] args) {
        int numRows = 10;
        int numCols = 10;
        int numTriples = 7; // Configure the number of subways
        JFrame frame = new JFrame("Custom Connected Subways Playing Field");
        ConnectedSubwaysPlayingField playingField = new ConnectedSubwaysPlayingField(numRows, numCols, numTriples);
        frame.add(playingField);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
