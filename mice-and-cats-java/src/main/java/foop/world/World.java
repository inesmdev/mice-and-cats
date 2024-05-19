package foop.world;

import foop.message.EntityUpdateMessage;
import foop.message.GameWorldMessage;
import foop.server.Player;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.time.Duration;
import java.util.List;
import java.util.*;

public class World {

    // 0=empty, +-subway ids, +means exit
    // int[y][x] row major
    private final int[][] grid;
    private final int[][] tempGrid;
    private final int numRows;
    private final int numCols;
    private final int numSubways;
    private final ArrayList<Entity> entities = new ArrayList<>();

    private final int type;

    private final List<Color> colors = new LinkedList<>(Arrays.asList(Color.red, Color.green, Color.blue, Color.yellow));
    private final List<Subway> subways = new LinkedList<>();


    public World(Random seed, int type, int numSubways, int numCols, int numRows) {

        grid = new int[numRows + 1][numCols + 1];
        tempGrid = new int[numRows + 1][numCols + 1];
        this.type = type;
        this.numSubways = numSubways;
        this.numCols = numCols;
        this.numRows = numRows;

        // Initialize the grid with zeros
        for (int i = 0; i < numRows + 1; i++) {
            for (int j = 0; j < numCols + 1; j++) {
                grid[i][j] = 0;
                tempGrid[i][j] = 0;
            }
        }
        placeConnectedSubways(seed);
        entities.add(new Entity(0, "cat", new Position(1, 1)));
    }

    public World(GameWorldMessage m) {
        subwayTiles = m.subwayTiles();
        subways = new Subway[m.subways().size() + 1];
        for (int i = 0; i < m.subways().size(); i++) {
            subways[i + 1] = m.subways().get(i);
        }
    }

    public void serverUpdate(HashSet<Player> players, Duration duration) {
        var r = new Random();

        var cat = entities.get(0);
        cat.setPosition(new Position(r.nextInt(subwayTiles[0].length), r.nextInt(subwayTiles.length)));

        var catUpdate = new EntityUpdateMessage(cat.getId(), cat.getName(), cat.getPosition());
        for (var player : players) {
            player.send(catUpdate);
        }
    }

    private void placeConnectedSubways(Random seed1) {
        java.util.List<Point> availableCells = new ArrayList<>();
        for (int i = 1; i < numRows; i++) {
            for (int j = 1; j < numCols; j++) {
                availableCells.add(new Point(i, j));
            }
        }

        Collections.shuffle(availableCells, seed1);

        for (int i = 1; i <= numSubways; i++) { //start with 1, since 0 is used for empty cells
            if (availableCells.isEmpty()) {
                break; // No more available cells
            }

            List<Point> subwayCells = new LinkedList<>();

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
                Position entry1 = null;
                Position entry2 = null;
                for (int j = 0; j < subwayCells.size(); j++) {
                    Point cell = subwayCells.get(j);

                    if (j == 0) {
                        grid[cell.x][cell.y] = -i;
                        entry1 = new Position(cell.x, cell.y);
                    }
                    if (j == subwayCells.size() - 1) {
                        grid[cell.x][cell.y] = -i;
                        entry2 = new Position(cell.x, cell.y);
                    } else {
                        grid[cell.x][cell.y] = i;
                    }
                }
                subways.add(new Subway(subways.size(), colors.get(i % 4), new Position[]{
                        entry1,
                        entry2,
                }));
            }
        }
    }

    private Point getNeighbor(Random seed1, List<Point> availableCells, Point cell, int color) {

        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(cell.x - 1, cell.y)); // Up
        neighbors.add(new Point(cell.x + 1, cell.y)); // Down
        neighbors.add(new Point(cell.x, cell.y - 1)); // Left
        neighbors.add(new Point(cell.x, cell.y + 1)); // Right

        Collections.shuffle(neighbors, seed1); // Randomize the order of neighbors

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
        return cell.x < numCols && cell.x > 0 && cell.y < numRows && cell.y > 0;
    }

    public void render(Graphics2D g, int w, int h) {

        int tileSize = Math.min(w / grid[0].length, h / grid.length);

        // Draw grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (this.type == 0) {
                    if (grid[i][j] == 0) {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                        g.setColor(Color.BLACK);
                        g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    } else if (grid[i][j] < 0) { //this is an exit
                        g.setColor(colors.get((grid[i][j] * -1) % 4));
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    } else {
                        g.setColor(colors.get(grid[i][j] % 4).darker());
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                } else if (type == 1) {
                    if (grid[i][j] < 0) {
                        g.setColor(colors.get((grid[i][j] * -1) % 4));
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                        g.setColor(Color.BLACK);
                        g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("default", Font.BOLD, 16));
        for (Entity entity : entities) {
            var bounds = g.getFontMetrics().getStringBounds(entity.getName(), g);
            int x = originX + tileSize * entity.getPosition().x() + tileSize / 2 - (int) (bounds.getWidth() / 2);
            int y = originY + tileSize * entity.getPosition().y() + tileSize / 2 + (int) (bounds.getHeight() / 2);
            g.drawString(entity.getName(), x, y);
        }
        }
    }

    public void entityUpdate(EntityUpdateMessage m) {
        if (m.id() < entities.size()) {
            var entity = entities.get(m.id());
            entity.setName(m.name());
            entity.setPosition(m.position());
        } else if (m.id() == entities.size()) {
            entities.add(new Entity(m.id(), m.name(), m.position()));
        } else {
            throw new IllegalStateException("Unexpected entity update message: " + m);
        }
    }

    public void sendTo(HashSet<Player> players) {
        var message = new GameWorldMessage(subwayTiles, Arrays.asList(subways).subList(1, subways.length));
        players.forEach(p -> p.send(message));

        for (Entity entity : entities) {
            var entityUpdate = new EntityUpdateMessage(entity.getId(), entity.getName(), entity.getPosition());
            players.forEach(p -> p.send(entityUpdate));
        }
    }
}
