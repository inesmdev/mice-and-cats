package foop.world;

import foop.Assets;
import foop.message.EntityUpdateMessage;
import foop.message.GameOverMessage;
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

    private final List<Color> colors = List.of(Color.red, Color.green, Color.blue, Color.yellow);
    private final HashMap<Integer, Subway> subways;
    private final HashMap<Position, Subway> cellToSubway;

    private Position nextCatGoal = null;

    public World(Random seed, int numSubways, int numCols, int numRows, HashSet<Player> players) {
        grid = new int[numRows][numCols];
        this.numCols = numCols;
        this.numRows = numRows;
        this.subways = new HashMap<>();
        this.cellToSubway = new HashMap<>();

        placeConnectedSubways(seed, numSubways);

        entities.add(new Entity(0, "cat", new Position(1, 1), false, false));

        for (Player player : players) {
            entities.add(new Entity(entities.size(), player.getName(), getRandomGroundPosition(seed), false, false));
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
        this.numRows = grid.length;
        this.numCols = grid[0].length;
    }

    public void afterEntityMoved(HashSet<Player> players) {
        // necessary if this is called after the game is already over
        if (players.isEmpty()) {
            return;
        }

        var cat = entities.get(0);
        for (Entity e : entities) {
            if (cat != e && !e.isDead() && cat.getPosition().equals(e.getPosition()) && cat.isUnderground() == e.isUnderground()) {
                e.setDead(true);
                var update = new EntityUpdateMessage(e.getId(), e.getName(), e.getPosition(), e.isUnderground(), e.isDead());
                // we copy to avoid iterator invalidation, when a player is removed
                for (var player : new ArrayList<>(players)) {
                    player.send(update);
                    if (player.getName().equals(e.getName())) {
                        player.gameOver(new GameOverMessage(GameOverMessage.Result.YOU_DIED));
                    }
                }
            }
        }

        // game-over: only one player left
        if (entities.stream().filter(e -> e.getId() != 0 && !e.isDead()).count() == 1) {
            var name = entities.stream().filter(e -> e.getId() != 0 && !e.isDead()).findFirst().get().getName();
            var player = players.stream().filter(p -> p.getName().equals(name)).findFirst().get();
            player.gameOver(new GameOverMessage(GameOverMessage.Result.ALL_BUT_YOU_DIED));
            return;
        }

        // game-over: all players in one tunnel
        if (entities.stream().filter(e -> e.getId() != 0 && !e.isDead() && getSubway(e) != 0).findFirst().orElse(null) instanceof Entity undead) {
            var subway = getSubway(undead);
            if (entities.stream().filter(e -> e.getId() != 0 && !e.isDead()).allMatch(e -> getSubway(e) == subway)) {
                var victoryMessage = new GameOverMessage(GameOverMessage.Result.VICTORY);
                entities.stream().filter(e -> e.getId() != 0 && !e.isDead()).forEach(e -> {
                    var player = players.stream().filter(p -> p.getName().equals(e.getName())).findFirst().get();
                    player.gameOver(victoryMessage);
                });
            }
        }
    }

    public void serverUpdate(HashSet<Player> players, Duration duration) {
        var r = new Random();

        var cat = entities.get(0);

        if (nextCatGoal == null) {
            nextCatGoal = new Position(r.nextInt(grid[0].length), r.nextInt(grid.length));
        }
        var dx = nextCatGoal.x() - cat.getPosition().x();
        var dy = nextCatGoal.y() - cat.getPosition().y();
        dx = Math.min(1, Math.max(-1, dx));
        dy = Math.min(1, Math.max(-1, dy));
        cat.setPosition(new Position(cat.getPosition().x() + dx, cat.getPosition().y() + dy));
        if (nextCatGoal.equals(cat.getPosition())) {
            nextCatGoal = null;
        }
        afterEntityMoved(players);

        var catUpdate = new EntityUpdateMessage(cat.getId(), cat.getName(), cat.getPosition(), cat.isUnderground(), cat.isDead());
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

    private Position getRandomGroundPosition(Random rand) {
        int randomX = rand.nextInt(this.numCols);
        int randomY = rand.nextInt(this.numRows);

        while (grid[randomY][randomX] != 0) {
            randomX = rand.nextInt(this.numCols);
            randomY = rand.nextInt(this.numRows);
        }
        return new Position(randomX, randomY);
    }

    private int getSubway(Entity e) {
        return e.isUnderground() ? Math.abs(grid[e.getPosition().y()][e.getPosition().x()]) : 0;
    }

    public void render(Graphics2D g, int w, int h, String playerName, boolean superVision) {

        int tileSize = Math.min(w / grid[0].length, h / grid.length);

        g.translate((w - tileSize * grid[0].length) / 2, (h - tileSize * grid.length) / 2);

        var playerEntity = entities.stream().filter(e -> e.getName().equals(playerName)).findFirst().orElse(null);
        if (playerEntity == null) {
            return; // we haven't received the entities yet
        }
        var playerSubway = getSubway(playerEntity);

        // Draw grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == 0) {
                    g.setColor(playerEntity.isUnderground() ? Color.BLACK : Color.WHITE);
                    g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                } else if (grid[i][j] < 0) { //this is an exit
                    g.setColor(colors.get((grid[i][j] * -1) % 4));
                    g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                } else {
                    var c = colors.get(grid[i][j] % 4).darker();
                    if (grid[i][j] == playerSubway) {
                        g.setColor(c);
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    } else {
                        g.setColor(playerEntity.isUnderground() ? Color.BLACK : Color.WHITE);
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
                        g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                }
                g.setColor(Color.BLACK);
                g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
            }
        }

        g.setFont(new Font("default", Font.BOLD, 12));
        for (Entity entity : entities) {
            if (getSubway(entity) == playerSubway || superVision) {
                var bounds = g.getFontMetrics().getStringBounds(entity.getName(), g);

                boolean isCat = entity.getId() == 0;
                int imageDown = isCat ? 0 : tileSize / 5;
                int textUp = tileSize / 3;

                if (entity.isDead()) {
                    var image = Assets.getInstance().getTombstone();
                    textUp = 0;
                    g.drawImage(image, tileSize * entity.getPosition().x(), tileSize * entity.getPosition().y(), tileSize, tileSize, null);
                } else {
                    var image = isCat ? Assets.getInstance().getCat() : Assets.getInstance().getMouse();
                    g.drawImage(image, tileSize * entity.getPosition().x(), tileSize * entity.getPosition().y() + imageDown, tileSize, tileSize, null);
                }

                if (!isCat) {
                    int x = tileSize * entity.getPosition().x() + tileSize / 2 - (int) (bounds.getWidth() / 2);
                    int y = tileSize * entity.getPosition().y() + tileSize / 2 + (int) (bounds.getHeight() / 2) - textUp;
                    var name = entity.isUnderground() ? "(" + entity.getName() + ")" : entity.getName();
                    g.setColor(playerEntity.isUnderground() ? Color.WHITE : Color.BLACK); // depends on how the player sees the background
                    g.drawString(name, x, y);
                }
            }
        }
    }

    public void entityUpdate(EntityUpdateMessage m) {
        if (m.id() < entities.size()) {
            var entity = entities.get(m.id());
            entity.setName(m.name());
            entity.setPosition(m.position());
            entity.setUnderground(m.isUnderground());
            entity.setDead(m.isDead());
        } else if (m.id() == entities.size()) {
            entities.add(new Entity(m.id(), m.name(), m.position(), m.isUnderground(), m.isDead()));
        } else {
            throw new IllegalStateException("Unexpected entity update message: " + m);
        }
    }

    public void sendTo(HashSet<Player> players) {
        var message = new GameWorldMessage(grid, subways.values().stream().toList());
        players.forEach(p -> p.send(message));

        for (Entity entity : entities) {
            var entityUpdate = new EntityUpdateMessage(entity.getId(), entity.getName(), entity.getPosition(), entity.isUnderground(), entity.isDead());
            players.forEach(p -> p.send(entityUpdate));
        }
    }

    public void movePlayer(HashSet<Player> players, Player player, int direction) {
        var entity = entities.stream().filter(e -> e.getName().equals(player.getName())).findFirst().get();
        if (entity.isDead()) {
            return;
        }
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
                var entityUpdate = new EntityUpdateMessage(entity.getId(), entity.getName(), entity.getPosition(), entity.isUnderground(), entity.isDead());
                players.forEach(p -> p.send(entityUpdate));
            }
        }

        afterEntityMoved(players);
    }

    public void killDisconnectedPlayer(String name, HashSet<Player> players) {
        var entity = entities.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
        if (entity != null && !entity.isDead()) {
            entity.setDead(true);
            var entityUpdate = new EntityUpdateMessage(entity.getId(), entity.getName(), entity.getPosition(), entity.isUnderground(), entity.isDead());
            players.forEach(p -> p.send(entityUpdate));
        }
    }
}
