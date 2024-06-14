package foop.world;

import java.awt.*;
import java.util.List;

public record Subway(int id, Color color, List<Position> exits, List<Position> subwayCells) {
}
