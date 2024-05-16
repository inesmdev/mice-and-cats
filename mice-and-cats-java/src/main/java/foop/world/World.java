package foop.world;

import foop.message.EntityUpdateMessage;
import foop.message.GameWorldMessage;
import foop.server.Player;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.*;

public class World {

    // 0=empty, +-subway ids, +means exit
    // int[y][x] row major
    private final int[][] subwayTiles;
    private final Subway[] subways;
    private final ArrayList<Entity> entities = new ArrayList<>();

    public World() {
        subwayTiles = new int[16][16];

        var s1 = new Subway(1, Color.ORANGE, List.of(
                new Position(3, 2),
                new Position(5, 2)
        ));
        subwayTiles[2][3] = 1;
        subwayTiles[2][4] = -1;
        subwayTiles[2][5] = 1;

        var s2 = new Subway(2, Color.RED, List.of(
                new Position(5, 4),
                new Position(5, 6)
        ));
        subwayTiles[5][4] = 2;
        subwayTiles[5][5] = -2;
        subwayTiles[5][6] = 2;

        subways = new Subway[]{null, s1, s2};

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

    public void render(Graphics2D g, int w, int h) {

        int tileSize = Math.min(w / subwayTiles[0].length, h / subwayTiles.length);
        int originX = (w - tileSize * subwayTiles[0].length) / 2;
        int originY = (h - tileSize * subwayTiles.length) / 2;

        for (int r = 0; r < subwayTiles.length; ++r) {
            for (int c = 0; c < subwayTiles[0].length; ++c) {
                int tile = subwayTiles[r][c];
                var subway = subways[Math.abs(tile)];

                if (subway != null) {
                    g.setColor(tile > 0 ? subway.color() : subway.color().darker());
                } else {
                    g.setColor((r + c) % 2 == 0 ? new Color(0xb58863) : new Color(0xf0d9b5));
                }
                g.fillRect(originX + r * tileSize, originY + c * tileSize, tileSize, tileSize);
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
