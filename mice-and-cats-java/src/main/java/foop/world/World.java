package foop.world;

import java.awt.*;

public class World {

    // 0=empty, +-subway ids, +means exit
    // int[y][x] row major
    private final int[][] subwayTiles;
    private final Subway[] subways;

    public World() {
        subwayTiles = new int[16][16];

        var s1 = new Subway(1, Color.ORANGE, new Position[]{
                new Position(3, 2),
                new Position(5, 2),
        });
        subwayTiles[2][3] = 1;
        subwayTiles[2][4] = -1;
        subwayTiles[2][5] = 1;

        var s2 = new Subway(2, Color.RED, new Position[]{
                new Position(5, 4),
                new Position(5, 6),
        });
        subwayTiles[5][4] = 2;
        subwayTiles[5][5] = -2;
        subwayTiles[5][6] = 2;

        subways = new Subway[]{null, s1, s2};
    }

    public void render(Graphics2D g, int w, int h) {

        int tileSize = Math.min(w / subwayTiles[0].length, h / subwayTiles.length);
        int originX = (w - tileSize * subwayTiles[0].length) / 2;
        int originY = (h - tileSize * subwayTiles.length) / 2;

        for (int r = 0; r < subwayTiles.length; ++r)
            for (int c = 0; c < subwayTiles[0].length; ++c) {
                int tile = subwayTiles[r][c];
                var subway = subways[Math.abs(tile)];

                if (subway != null) {
                    g.setColor(tile > 0 ? subway.color() : subway.color().darker());
                } else {
                    g.setColor((r + c) % 2 == 0 ? Color.GRAY : Color.WHITE);
                }
                g.fillRect(originX + r * tileSize, originY + c * tileSize, tileSize, tileSize);
            }
    }
}
