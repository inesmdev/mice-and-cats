package foop.views;

import foop.Client;
import foop.message.PlayerCommandMessage;
import foop.message.SetReadyForGameMessage;
import foop.world.World;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Slf4j
public class BoardView extends JPanel {

    private static final String ACTION_UP = "ACTION_UP";
    private static final String ACTION_DOWN = "ACTION_DOWN";
    private static final String ACTION_RIGHT = "ACTION_RIGHT";
    private static final String ACTION_LEFT = "ACTION_LEFT";
    private final GameFrame frame;
    private final JButton readyButton;
    private boolean superVision;
    private boolean started;

    public BoardView(GameFrame frame) {
        this.frame = frame;
        readyButton = new JButton();
        render();

        getActionMap().put(ACTION_UP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(ACTION_UP);
                var playerUpdate = new PlayerCommandMessage(1);
                frame.getClient().send(playerUpdate);

            }
        });
        getActionMap().put(ACTION_DOWN, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(ACTION_DOWN);
                var playerUpdate = new PlayerCommandMessage(3);
                frame.getClient().send(playerUpdate);
            }
        });
        getActionMap().put(ACTION_RIGHT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(ACTION_RIGHT);
                var playerUpdate = new PlayerCommandMessage(2);
                frame.getClient().send(playerUpdate);
            }
        });
        getActionMap().put(ACTION_LEFT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(ACTION_LEFT);
                var playerUpdate = new PlayerCommandMessage(4);
                frame.getClient().send(playerUpdate);
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('w'), ACTION_UP);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), ACTION_UP);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), ACTION_DOWN);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), ACTION_DOWN);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('a'), ACTION_LEFT);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), ACTION_LEFT);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('d'), ACTION_RIGHT);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), ACTION_RIGHT);
    }

    public void render() {

        setLayout(new BorderLayout());

        JButton stopButton = new JButton("Stop");

        JComponent component = new JComponent() {
            @Override
            public void paint(Graphics graphics) {
                repaint(); // already request next repaint
                Graphics2D g = (Graphics2D) graphics;
                World world = frame.getClient().getWorld();
                if (world != null) {
                    readyButton.setText("Started");
                    started = true;
                    world.render(g, getWidth(), getHeight(), frame.getClient().getPlayerName(), superVision);
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

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        var visionCheckbox = new JCheckBox("Super vision", superVision);
        visionCheckbox.addActionListener(e -> superVision = visionCheckbox.isSelected());
        sidePanel.add(visionCheckbox);

        panel.add(sidePanel, BorderLayout.CENTER);

        readyButton.addActionListener(e -> {
            readyButton.setEnabled(false);
            readyButton.setText("Waiting");
            log.info("Starting game...");
            frame.getClient().send(new SetReadyForGameMessage(true));
        });

        stopButton.addActionListener(e -> {
            log.info("Stopping game");
            frame.getClient().exitGame();
            if (started) {
                frame.showGameOverDeathView(false);
            } else {
                frame.showTitleScreenView();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(readyButton);
        buttonPanel.add(stopButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.EAST);

    }

    public void startNewGame() {
        readyButton.setEnabled(true);
        readyButton.setText("Ready");
        started = false;
    }

}
