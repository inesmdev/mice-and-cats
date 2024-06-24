package foop.views;

import foop.message.AvailableGamesMessage;
import foop.message.PlayerCommandMessage;
import foop.message.SetReadyForGameMessage;
import foop.message.VoteMessage;
import foop.world.World;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

@Slf4j
public class BoardView extends JPanel {

    private static final String ACTION_UP = "ACTION_UP";
    private static final String ACTION_DOWN = "ACTION_DOWN";
    private static final String ACTION_RIGHT = "ACTION_RIGHT";
    private static final String ACTION_LEFT = "ACTION_LEFT";
    private final GameFrame frame;
    private final JButton readyButton;
    private final JTextPane playersTextPane;
    private final JPanel votingPanel = new JPanel();
    private final ArrayList<JButton> votingButtons = new ArrayList<>();
    private boolean superVision;
    private boolean started;


    public BoardView(GameFrame frame) {
        this.frame = frame;
        readyButton = new JButton();
        playersTextPane = new JTextPane();
        playersTextPane.setEditable(false);
        playersTextPane.setFocusable(false);
        votingPanel.setLayout(new BoxLayout(votingPanel, BoxLayout.X_AXIS));
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
                    if (!started) {
                        readyButton.setText("Started");
                        started = true;

                        var subways = frame.getClient().getWorld().getSubways();
                        if (votingButtons.size() < subways.size()) { //only do this once
                            subways.keySet().forEach(key -> {
                                JButton btn = new JButton("U" + String.valueOf(subways.get(key).id()));
                                btn.setBackground(subways.get(key).color());
                                btn.addActionListener(e -> {
                                    frame.getClient().send(new VoteMessage(Integer.parseInt(btn.getText().substring(1))));
                                });
                                votingButtons.add(btn);
                            });

                            votingButtons.forEach(votingPanel::add);
                        }
                    }
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
        visionCheckbox.addActionListener(e -> {
            superVision = visionCheckbox.isSelected();
            BoardView.this.grabFocus();
        });
        sidePanel.add(visionCheckbox);

        sidePanel.add(playersTextPane);

        sidePanel.add(votingPanel);
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
        votingPanel.removeAll();
        votingButtons.clear();
        started = false;
    }

    public void updateLobby(AvailableGamesMessage m) {
        var text = new StringBuilder();
        var gameName = frame.getClient().getGameName();
        if (gameName != null) {
            for (AvailableGamesMessage.Game game : m.games()) {
                if (game.name().equals(gameName)) {
                    text.append("Duration: ");
                    text.append(game.duration().toMinutes());
                    text.append("m ");
                    text.append(game.duration().toSecondsPart());
                    text.append("s\n");
                    text.append("Players:\n");
                    for (AvailableGamesMessage.PlayerInfo player : game.players()) {
                        text.append("    ");
                        text.append(player.name());
                        text.append(player.ready() ? " (ready)" : " (not-ready)");
                        text.append("\n");
                    }
                    break;
                }
            }
        }
        playersTextPane.setText(text.toString());
    }
}
