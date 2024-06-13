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

    public void load() throws IOException {
        titleScreenBackground = readImageResource("/images/titlescreen.jpeg");
    }

    private Image readImageResource(String resource) throws IOException {
        // TODO: maybe use the asynchronous way of loading images?
        // return Toolkit.getDefaultToolkit().createImage(getClass().getResource(resource));
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resource), "Could not find resource " + resource));
    }
}
