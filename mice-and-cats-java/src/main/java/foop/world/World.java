package foop.world;

import foop.message.EntityUpdateMessage;
import foop.message.GameWorldMessage;
import foop.server.Player;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.*;

@Slf4j
public class World {

    // 0=empty, +-subway ids, +means exit
    // int[y][x] row major
    private final int[][] grid;
    private final int numRows;
    private final int numCols;
    @Getter
    private final ArrayList<Entity> entities = new ArrayList<>();

    private final int type;

    private final List<Color> colors = List.of(Color.red, Color.green, Color.blue, Color.yellow);
    private final HashMap<Integer, Subway> subways;
    private final HashMap<Position, Subway> cellToSubway;


    public World(Random seed, int type, int numSubways, int numCols, int numRows, HashSet<Player> players) {

        grid = new int[numRows][numCols];
        this.type = type;
        this.numCols = numCols;
        this.numRows = numRows;
        this.subways = new HashMap<>();
        this.cellToSubway = new HashMap<>();

        placeConnectedSubways(seed, numSubways);

        entities.add(new Entity(0, "cat", new Position(1, 1), false));

        for (Player player : players) {
            entities.add(new Entity(entities.size(), player.getName(), getRandomGroundPosition(), false));
        }
    }

    public World(GameWorldMessage m) {
        grid = m.subwayTiles();
        subways = new HashMap<>();
        cellToSubway = new HashMap<>();
        m.subways().forEach(s ->
        {
            subways.put(s.id(), s);
            s.subwayCells().forEach(c -> cellToSubway.put(c, s));
        });
        this.type = 0;
        this.numRows = grid.length;
        this.numCols = grid[0].length;


    }

    public void serverUpdate(HashSet<Player> players, Duration duration) {
        var r = new Random();

        var cat = entities.get(0);
        cat.setPosition(new Position(r.nextInt(grid[0].length), r.nextInt(grid.length)));

        var catUpdate = new EntityUpdateMessage(cat.getId(), cat.getName(), cat.getPosition(), cat.isUnderground());
        for (var player : players) {
            player.send(catUpdate);
        }
    }

    private void placeConnectedSubways(Random seed1, int numSubways) {
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
                Position entry1 = null;
                Position entry2 = null;
                for (int j = 0; j < subwayCells.size(); j++) {
                    Position cell = subwayCells.get(j);
                    if (j == 0) {
                        grid[cell.y()][cell.x()] = -i;
                        entry1 = cell;
                    } else if (j == subwayCells.size() - 1) {
                        grid[cell.y()][cell.x()] = -i;
                        entry2 = cell;
                    } else {
                        grid[cell.y()][cell.x()] = i;
                    }
                }
                var newSubway = new Subway(subways.size(), colors.get(i % 4), List.of(entry1, entry2), subwayCells.stream().toList());
                subways.put(newSubway.id(), newSubway);
                newSubway.subwayCells().forEach(c -> cellToSubway.put(c, newSubway));

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

    private Position getRandomGroundPosition() {
        Random rand = new Random();
        int randomX = rand.nextInt(this.numCols);
        int randomY = rand.nextInt(this.numRows);

        while (grid[randomY][randomX] != 0) {
            randomX = rand.nextInt(this.numCols);
            randomY = rand.nextInt(this.numRows);
        }
        return new Position(randomX, randomY);
    }

    public void render(Graphics2D g, int w, int h) {

        int tileSize = Math.min(w / grid[0].length, h / grid.length);

        g.translate((w - tileSize * grid[0].length) / 2, (h - tileSize * grid.length) / 2);


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
            int x = tileSize * entity.getPosition().x() + tileSize / 2 - (int) (bounds.getWidth() / 2);
            int y = tileSize * entity.getPosition().y() + tileSize / 2 + (int) (bounds.getHeight() / 2);
            g.drawString(entity.getName(), x, y);
        }

    }

    public void entityUpdate(EntityUpdateMessage m) {
        if (m.id() < entities.size()) {
            var entity = entities.get(m.id());
            entity.setName(m.name());
            entity.setPosition(m.position());
        } else if (m.id() == entities.size()) {
            entities.add(new Entity(m.id(), m.name(), m.position(), m.isUnderground()));
        } else {
            throw new IllegalStateException("Unexpected entity update message: " + m);
        }
    }

    public void sendTo(HashSet<Player> players) {
        var message = new GameWorldMessage(grid, subways.values().stream().toList());
        players.forEach(p -> p.send(message));

        for (Entity entity : entities) {
            var entityUpdate = new EntityUpdateMessage(entity.getId(), entity.getName(), entity.getPosition(), entity.isUnderground());
            players.forEach(p -> p.send(entityUpdate));
        }
    }

    public void movePlayer(HashSet<Player> players, Player player, int direction) {

        var entity = entities.stream().filter(e -> e.getName().equals(player.getName())).findFirst().get();
        var position = switch (direction) {
            case 1 -> new Position(entity.getPosition().x(), entity.getPosition().y() - 1);
            case 2 -> new Position(entity.getPosition().x() + 1, entity.getPosition().y());
            case 3 -> new Position(entity.getPosition().x(), entity.getPosition().y() + 1);
            case 4 -> new Position(entity.getPosition().x() - 1, entity.getPosition().y());
            default -> throw new IllegalArgumentException("Illegal Direction: " + direction);
        };

        boolean isMoveLegal = false;
        boolean isUnderground = entity.isUnderground();

        if (isWithinGrid(position)) {
            int newTile = grid[position.y()][position.x()];
            int oldTile = grid[entity.getPosition().y()][entity.getPosition().x()];

            if (!isUnderground) {
                isMoveLegal = true;
                if (newTile < 0) {
                    isUnderground = true;
                }
            } else {
                boolean sameSubway = newTile == oldTile || newTile == -oldTile;
                isMoveLegal = oldTile < 0 || sameSubway;
                isUnderground = newTile < 0 || sameSubway;
            }

            // sanity check
            if (entity.isUnderground() && cellToSubway.get(entity.getPosition()) == null) {
                throw new RuntimeException("Entity is underground but not in subway");
            }

            if (isMoveLegal) {
                // sanity check
                if (isUnderground && (cellToSubway.get(position) == null)) {
                    throw new RuntimeException("Entity is underground but not in subway");
                }

                entity.setPosition(position);
                entity.setUnderground(isUnderground);
                var entityUpdate = new EntityUpdateMessage(entity.getId(), entity.getName(), entity.getPosition(), entity.isUnderground());
                players.forEach(p -> p.send(entityUpdate));
            }

        }


    }
}
