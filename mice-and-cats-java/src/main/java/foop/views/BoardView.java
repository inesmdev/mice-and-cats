package foop.views;

import foop.message.ExitGameMessage;
import foop.message.SetReadyForGameMessage;
import foop.world.World;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Slf4j
public class BoardView extends JPanel {

    private final GameFrame frame;
    private static final String ACTION_UP = "ACTION_UP";

    public BoardView(GameFrame frame) {
        this.frame = frame;
        render();

        getActionMap().put(ACTION_UP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(ACTION_UP);
            }
        });
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('w'), ACTION_UP);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), ACTION_UP);
    }

    public void render() {

        setLayout(new BorderLayout());

        JButton readyButton = new JButton("Ready");
        JButton stopButton = new JButton("Stop");

        JComponent component = new JComponent() {
            @Override
            public void paint(Graphics graphics) {
                repaint(); // already request next repaint
                Graphics2D g = (Graphics2D) graphics;
                World world = frame.getClient().getWorld();
                if (world != null) {
                    readyButton.setText("Started");
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


        readyButton.addActionListener(e -> {
            readyButton.setEnabled(false);
            readyButton.setText("Waiting");
            log.info("Starting game...");
            frame.send(new SetReadyForGameMessage(true));
        });


        stopButton.addActionListener(e -> {
            readyButton.setEnabled(true);
            readyButton.setText("Ready");
            log.info("Stopping game");
            // Todo:
            //  When this button is pressed the cat should stop moving.
            //  in the other window and change back to the starting window...
            frame.send(new ExitGameMessage(frame.getGameName()));
            frame.showTitleScreenView();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(readyButton);
        buttonPanel.add(stopButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);

    }


}
