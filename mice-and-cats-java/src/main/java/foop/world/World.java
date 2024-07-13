package foop.world;

import foop.Assets;
import foop.message.EntityUpdateMessage;
import foop.message.GameOverMessage;
import foop.message.GameWorldMessage;
import foop.message.Message;
import foop.server.Player;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.*;

import static foop.world.Type.CAT;
import static foop.world.Type.MOUSE;

@Slf4j
public class World {

    // 0=empty, +-subway ids, +means exit
    // int[y][x] row major
    private final int[][] grid;
    private final int numRows;
    private final int numCols;
    @Getter
    private final List<Entity> entities = new ArrayList<>();

    private final List<Color> colors = List.of(Color.red, Color.green, Color.blue, Color.yellow);
    @Getter
    private final HashMap<Integer, Subway> subways;
    private final HashMap<Position, Subway> cellToSubway;
    private final Map<Integer, Position> catLastSeen = new HashMap<>();
    private long countInMySubway = 0;
    @Getter
    private final Duration duration;

    public World(Random seed, int numSubways, int numCols, int numRows, HashSet<Player> players, Duration duration) {
        grid = new int[numRows][numCols];
        this.numCols = numCols;
        this.numRows = numRows;
        this.subways = new HashMap<>();
        this.cellToSubway = new HashMap<>();
        this.duration = duration;

        SubwayBuilder subwayBuilder = new SubwayBuilder(this, numRows, numCols);
        subwayBuilder.placeConnectedSubways(seed, numSubways);
        entities.add(new Entity(0, CAT, "cat", new Position(1, 1), false, false, -1));

        int subway = 0;
        for (Player player : players) {
            var cells = getSubways().get(subway + 1).subwayCells(); //subwayIDs start with 1
            subway = (subway + 1) % getSubways().size();
            var tile = cells.get(seed.nextInt(cells.size()));
            entities.add(new Entity(entities.size(), MOUSE, player.getName(), tile, true, false, -1));
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
        this.duration = Duration.ofSeconds(m.durationSec());
        this.numRows = grid.length;
        this.numCols = grid[0].length;
    }

    public void afterEntityMoved(HashSet<Player> players) {
        // necessary if this is called after the game is already over
        if (players.isEmpty()) {
            return;
        }


        var cats = entities.stream().filter(entity -> entity.getType() == CAT).toList();
        for (Entity e : entities) {
            if (e.getType() == MOUSE
                    && !e.isDead()
                    && cats.stream().filter(cat -> cat.isUnderground() == e.isUnderground()).map(Entity::getPosition).anyMatch(c -> c.equals(e.getPosition()))) {
                // entity was killed by the cat...
                e.setDead(true);
                broadcastMsg(players, new EntityUpdateMessage(e));
                // we copy to avoid iterator invalidation, when a player is removed


                players.stream().filter(player -> player.getName().equals(e.getName())).findFirst().ifPresent(
                        tmp -> tmp.gameOver(new GameOverMessage(GameOverMessage.Result.YOU_DIED))
                );
            }
        }

        var countLivingMice = entities.stream().filter(e -> e.getType() != CAT && !e.isDead()).count();

        // game-over: only one player left
        if (countLivingMice == 1) {
            entities.stream().filter(e -> e.getType() != CAT && !e.isDead()).findFirst().
                    flatMap(entity -> players.stream().filter(p -> p.getName().equals(entity.getName())).findFirst()).ifPresent(player ->
                            player.gameOver(new GameOverMessage(GameOverMessage.Result.ALL_BUT_YOU_DIED)));
            return;
        }

        // game-over: all players in one tunnel
        if (countLivingMice > 1) {
            if (entities.stream().filter(e -> e.getType() != CAT && !e.isDead() && getSubway(e) != 0).findFirst().orElse(null) instanceof Entity undead) {
                var subway = getSubway(undead);
                if (entities.stream().filter(e -> e.getType() != CAT && !e.isDead()).allMatch(e -> getSubway(e) == subway)) {
                    var victoryMessage = new GameOverMessage(GameOverMessage.Result.VICTORY);
                    entities.stream().filter(e -> e.getType() != CAT && !e.isDead()).forEach(e -> {
                        players.stream().filter(p -> p.getName().equals(e.getName())).findFirst().ifPresent(player ->
                                player.gameOver(victoryMessage)
                        );
                    });
                }
            }
        }
    }

    public void serverUpdate(HashSet<Player> players) {

        var cats = entities.stream().filter(e -> e.getType() == CAT).toList();
        for (Entity cat : cats) {
            generateNewCatPosition(cat);
            afterEntityMoved(players);
            var msg = new EntityUpdateMessage(cat);
            broadcastMsg(players, msg);
        }
    }

    private void generateNewCatPosition(Entity cat) {
        Position nextCatGoal = findNearestPlayer();
        cat.setPosition(cat.getPosition().stepTowards(nextCatGoal));
    }

    private Position findNearestPlayer(){
        var visible = entities.stream().filter(e -> e.getType() == MOUSE && !e.isUnderground()).toList();
        if (!visible.isEmpty()){
            Entity cat = entities.get(0);
            Entity nearest = visible.get(0);
            double nearestDistance = cat.getPosition().distanceTo(nearest.getPosition());

            for (int i = 1; i < visible.size(); i++) {
                Entity curr = visible.get(i);
                double d = cat.getPosition().distanceTo(curr.getPosition());
                if(nearestDistance > d){
                    nearestDistance = d;
                    nearest = curr;
                }
            }

            return nearest.getPosition();
        }
        //if no players are visible, move randomly
        var r = new Random();
        return new Position(r.nextInt(grid[0].length), r.nextInt(grid.length));
    }

    private void broadcastMsg(HashSet<Player> players, Message msg) {
        players.forEach(player -> player.send(msg));
    }

    private boolean isWithinGrid(Position cell) {
        return cell.x() < numCols && cell.x() >= 0 && cell.y() < numRows && cell.y() >= 0;
    }

    public void addSubway(List<Position> subwayCells, int subwayNumber) {
        Position entry1 = null;
        Position entry2 = null;
        for (int j = 0; j < subwayCells.size(); j++) {
            Position cell = subwayCells.get(j);
            if (j == 0) {
                grid[cell.y()][cell.x()] = -subwayNumber;
                entry1 = cell;
            } else if (j == subwayCells.size() - 1) {
                grid[cell.y()][cell.x()] = -subwayNumber;
                entry2 = cell;
            } else {
                grid[cell.y()][cell.x()] = subwayNumber;
            }
        }
        var newSubway = new Subway(subwayNumber, colors.get(subwayNumber % 4), List.of(entry1, entry2), subwayCells.stream().toList());
        subways.put(newSubway.id(), newSubway);
        newSubway.subwayCells().forEach(c -> cellToSubway.put(c, newSubway));
    }

    private int getSubway(Entity e) {
        return e.isUnderground() ? Math.abs(grid[e.getPosition().y()][e.getPosition().x()]) : 0;
    }

    /**
     * Methode to render this world in the UI.
     *
     * @param g           view field to add the rendered world
     * @param w           width of the game field
     * @param h           height of the game field
     * @param playerName  playerName to display
     * @param superVision parameter for development, which displays all entities all the time
     */
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

        // Draw entities (Cat's and Mice)
        g.setFont(new Font("default", Font.BOLD, 12));
        long countOnSameSubway = entities.stream().filter(e -> getSubway(e) == playerSubway).count();
        for (Entity entity : entities) {
            boolean isMouse = entity.getType() == MOUSE;
            var image = Assets.getInstance().getCat();
            int imageDown = isMouse && !entity.isDead() ? tileSize / 5 : 0;

            // update the cat position if you see it:


            // draw all entities on the same Subway or on the surface as this player
            if (getSubway(entity) == playerSubway || superVision) {
                if (!isMouse) {
                    catLastSeen.put(entity.getId(), entity.getPosition());
                }
                var bounds = g.getFontMetrics().getStringBounds(entity.getName(), g);

                int textUp = tileSize / 3;

                if (isMouse) {
                    image = Assets.getInstance().getMouse();
                    if (entity.isDead()) image = Assets.getInstance().getTombstone();
                }
                g.drawImage(image, tileSize * entity.getPosition().x(), tileSize * entity.getPosition().y() + imageDown, tileSize, tileSize, null);


                // if it's a Mice display the name:
                if (isMouse && !entity.isDead()) {
                    int x = tileSize * entity.getPosition().x() + tileSize / 2 - (int) (bounds.getWidth() / 2);
                    int y = tileSize * entity.getPosition().y() + tileSize / 2 + (int) (bounds.getHeight() / 2) - textUp;
                    var name = entity.isUnderground() ? "(" + entity.getName() + ")" : entity.getName();
                    g.setColor(playerEntity.isUnderground() ? Color.WHITE : Color.BLACK); // depends on how the player sees the background
                    g.drawString(name, x, y);

                    if (entity.getVote() != -1) {
                        // Draw a circle next to the entity name
                        int circleDiameter = 14; // Adjust the diameter as needed
                        int circleX = x + (int) bounds.getWidth() / 2 - circleDiameter / 2; // Adjust the offset as needed
                        int circleY = y - (int) bounds.getHeight() - textUp - 4; // Adjust the offset as needed

                        g.setColor(subways.get(entity.getVote()).color()); // Set the color of the circle
                        g.fillOval(circleX, circleY, circleDiameter, circleDiameter);
                    }
                }
            }

            // if the player is underground and was on the surface once draw the last cat position
            if (playerEntity.isUnderground() && countOnSameSubway > countInMySubway) {
                // update cat last seen position
                if (!isMouse) {
                    catLastSeen.put(entity.getId(), entity.getPosition());
                }
            }

            if (playerEntity.isUnderground()) {
                if (catLastSeen.containsKey(entity.getId())) {
                    g.drawImage(image, tileSize * catLastSeen.get(entity.getId()).x(), tileSize * catLastSeen.get(entity.getId()).y() + imageDown, tileSize, tileSize, null);
                }
            }

        }
        countInMySubway = countOnSameSubway;
    }

    public void entityUpdate(EntityUpdateMessage m) {
        if (m.id() < entities.size()) {
            var entity = entities.get(m.id());
            entity.setName(m.name());
            entity.setPosition(m.position());
            entity.setUnderground(m.isUnderground());
            entity.setDead(m.isDead());
            entity.setVote(m.vote());
        } else if (m.id() == entities.size()) {
            entities.add(new Entity(m.id(), m.type(), m.name(), m.position(), m.isUnderground(), m.isDead(), m.vote()));
        } else {
            throw new IllegalStateException("Unexpected entity update message: " + m);
        }
    }

    public void sendTo(HashSet<Player> players) {
        var message = new GameWorldMessage(grid, subways.values().stream().toList(), duration.toSeconds());
        broadcastMsg(players, message);

        for (Entity entity : entities) {
            var entityUpdate = new EntityUpdateMessage(entity);
            broadcastMsg(players, entityUpdate);
        }
    }

    public void movePlayer(HashSet<Player> players, Player player, int direction) {
        var entity = entities.stream().filter(e -> e.getName().equals(player.getName())).findFirst().orElse(null);
        if (entity == null || entity.isDead()) {
            return;
        }
        var position = switch (direction) {
            case 1 -> new Position(entity.getPosition().x(), entity.getPosition().y() - 1);
            case 2 -> new Position(entity.getPosition().x() + 1, entity.getPosition().y());
            case 3 -> new Position(entity.getPosition().x(), entity.getPosition().y() + 1);
            case 4 -> new Position(entity.getPosition().x() - 1, entity.getPosition().y());
            default -> throw new IllegalArgumentException("Illegal Direction: " + direction);
        };

        boolean isMoveLegal;
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
                var entityUpdate = new EntityUpdateMessage(entity);
                broadcastMsg(players, entityUpdate);
            }
        }

        afterEntityMoved(players);
    }

    public void killDisconnectedPlayer(String name, HashSet<Player> players) {
        var entity = entities.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
        if (entity != null && !entity.isDead()) {
            entity.setDead(true);
            var entityUpdate = new EntityUpdateMessage(entity);
            broadcastMsg(players, entityUpdate);
        }
    }

    public void updateVote(HashSet<Player> players, Player player, int vote) {
        log.info("Entity " + player.getName() + " votes " + vote);
        var entity = entities.stream().filter(e -> e.getName().equals(player.getName())).findFirst().get();
        if (entity.isDead()) {
            return;
        }
        entity.setVote(vote);
        var entityUpdate = new EntityUpdateMessage(entity);
        players.forEach(p -> p.send(entityUpdate));
    }
}
