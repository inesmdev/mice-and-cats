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

    public void load() throws IOException {
        titleScreenBackground = readImageResource("/images/titlescreen.jpeg");
        mouse = readImageResourceAsync("/images/mouse.png");
        cat = readImageResourceAsync("/images/cat.png");
    }

    private Image readImageResource(String resource) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resource), "Could not find resource " + resource));
    }

    private Image readImageResourceAsync(String resource) {
        return Toolkit.getDefaultToolkit().createImage(getClass().getResource(resource));
    }
}
