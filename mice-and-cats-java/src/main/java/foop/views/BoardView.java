package foop.views;

import foop.actions.Exit;
import foop.message.AvailableGamesMessage;
import foop.message.SetReadyForGameMessage;
import foop.world.World;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

import static foop.message.Message.serialize;

@Slf4j
public class BoardView extends JPanel {

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
                repaint(); // already request next repaint
                Graphics2D g = (Graphics2D) graphics;
                World world = frame.getClient().getWorld();
                if (world != null) {
                    world.render(g, getWidth(), getHeight());
                } else {
                    g.clearRect(0, 0, getWidth(), getHeight());
                }
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
            frame.send(new SetReadyForGameMessage(true));
        });
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            log.info("Stopping game");
            // Todo:
            //  When this button is pressed the cat should stop moving.
            //  in the other window and change back to the starting window...
        });
        stopButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);

    }


}
