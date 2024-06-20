package foop.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SubwayBuilder {

    private final int numRows;
    private final int numCols;
    private final World world;



    public SubwayBuilder(World world, int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.world = world;
    }

    public void placeConnectedSubways(Random seed1, int numSubways) {
        var tempGrid = new int[numRows][numCols];
        java.util.List<Position> availableCells = new ArrayList<>();
        for (int x = 1; x < numCols - 1; x++) {
            for (int y = 1; y < numRows - 1; y++) {
                availableCells.add(new Position(x, y));
            }
        }

        Collections.shuffle(availableCells, seed1);

        for (int i = 1; i <= numSubways; i++) { //start with 1, since 0 is used for empty cells
            if (availableCells.isEmpty()) {
                break; // No more available cells
            }

            List<Position> subwayCells = new ArrayList<>();

            Position cell1 = availableCells.removeFirst();
            subwayCells.add(cell1);
            tempGrid[cell1.y()][cell1.x()] = i;
            Position currCell = cell1;
            int numSubwayCells = 0;
            while (currCell != null && numSubwayCells < 8) {
                Position newCell = getNeighbor(seed1, availableCells, currCell, i, tempGrid);
                if (newCell != null) {
                    tempGrid[newCell.y()][newCell.x()] = i;
                    subwayCells.add(newCell);
                }
                currCell = newCell;
                numSubwayCells++;
            }

            //subway has to be at least 4 cells long
            if (subwayCells.size() > 3) {
                world.addSubway(subwayCells, i);
            }
        }
    }

    private Position getNeighbor(Random seed1, List<Position> availableCells, Position cell, int color, int[][] tempGrid) {

        List<Position> neighbors = new ArrayList<>();
        neighbors.add(new Position(cell.x() - 1, cell.y()));
        neighbors.add(new Position(cell.x() + 1, cell.y()));
        neighbors.add(new Position(cell.x(), cell.y() - 1));
        neighbors.add(new Position(cell.x(), cell.y() + 1));

        Collections.shuffle(neighbors, seed1); // Randomize the order of neighbors

        for (Position neighbor : neighbors) {
            if (availableCells.contains(neighbor) && isWithinGrid(neighbor) && noUturn(neighbor, color, tempGrid)) {
                availableCells.remove(neighbor); // Remove the cell from available cells
                return neighbor;

            }
        }

        return null; // No available neighbor found within the distance limit
    }

    private boolean noUturn(Position cell, int color, int[][] tempGrid) {
        int count = 0;

        //check in all 4 directions whether a cell of the same color is a neighbor
        if (tempGrid[cell.y() - 1][cell.x()] == color) {
            count += 1;
        }
        if (tempGrid[cell.y() + 1][cell.x()] == color) {
            count += 1;
        }
        if (tempGrid[cell.y()][cell.x() + 1] == color) {
            count += 1;
        }
        if (tempGrid[cell.y()][cell.x() - 1] == color) {
            count += 1;
        }

        return count <= 1;
    }

    private boolean isWithinGrid(Position cell) {
        return cell.x() < numCols && cell.x() >= 0 && cell.y() < numRows && cell.y() >= 0;
    }

}
