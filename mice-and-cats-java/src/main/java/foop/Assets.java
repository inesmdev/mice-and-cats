package foop;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

@Getter
public class Assets {
    private static final Assets INSTANCE = new Assets();

    public static Assets getInstance() {
        return INSTANCE;
    }

    private Image titleScreenBackground;
    private Image mouse;
    private Image cat;
    private Image victory;
    private Image death;
    private Image tombstone;
    private Image outOfTime;

    public void load() throws IOException {
        titleScreenBackground = readImageResource("/images/titlescreen.jpeg");

        // TODO we could load these in a thread
        mouse = readImageResource("/images/mouse.png");
        cat = readImageResource("/images/cat.png");
        victory = readImageResource("/images/victory.jpeg");
        death = readImageResource("/images/death.jpeg");
        tombstone = readImageResource("/images/tombstone.jpeg");
        outOfTime = readImageResource("/images/time.jpeg");
    }

    private Image readImageResource(String resource) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resource), "Could not find resource " + resource));
    }

//    private Image readImageResourceAsync(String resource) {
//        return Toolkit.getDefaultToolkit().createImage(getClass().getResource(resource));
//    }
}
