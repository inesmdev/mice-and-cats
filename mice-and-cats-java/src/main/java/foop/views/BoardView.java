package foop.views;

import foop.world.World;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class BoardView extends JPanel {

    private final static Logger LOG = LoggerFactory.getLogger(BoardView.class);

    private GameFrame frame;

    public BoardView(GameFrame frame) {
        this.frame = frame;
        render();
    }

    public void render() {

        setLayout(new BorderLayout());

        JComponent component = new JComponent() {
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                World world = new World();
                world.render(g, getWidth(), getHeight());
            }
        };
        component.setPreferredSize(new Dimension(500, 500));
        add(component, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setSize(300, 500);
        panel.setLayout(new BorderLayout());

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            log.info("Starting game...");
        });
        JButton stopButton = new JButton();
        stopButton.addActionListener(e -> {
            log.info("Stopping game");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);

    }

}
