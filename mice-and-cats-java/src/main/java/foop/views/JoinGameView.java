package foop.views;

import foop.message.AvailableGamesMessage;
import foop.message.JoinGameMessage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class JoinGameView extends JPanel {


    private final GameFrame frame;

    private final JList<AvailableGamesMessage.Game> lobbyList;
    private final DefaultListModel<AvailableGamesMessage.Game> lobbyListModel = new DefaultListModel<>();

    private boolean firstTime = true;

    public JoinGameView(GameFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());

        JPanel headPanel = getHeadPanel();
        add(headPanel, BorderLayout.NORTH);

        JButton startButton = new JButton("Join");

        lobbyList = new JList<>(lobbyListModel);
        lobbyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lobbyList.addListSelectionListener(e -> startButton.setEnabled(true));
        lobbyList.setBounds(100, 100, 200, 30);
        lobbyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                var component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AvailableGamesMessage.Game g) {
                    setText(g.name() + ":" +
                            " [" + String.join(", ", g.players()) + "]" +
                            " (" + g.duration().toMinutes() + "m " + g.duration().getSeconds() + "s)" +
                            (g.started() ? " (started)" : "")
                    );
                }
                return component;
            }
        });
        add(lobbyList, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(lobbyList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        startButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            AvailableGamesMessage.Game game = lobbyList.getSelectedValue();
            frame.setGameName(game.name());
            if (firstTime) {

                firstTime = false;
            }
            frame.send(new JoinGameMessage(game.name()));
            frame.showBoardView();
        });
        startButton.setEnabled(false);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.add(startButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel getHeadPanel() {
        JPanel headPanel = new JPanel();
        headPanel.setLayout(new BorderLayout());

        JButton returnButton = new JButton("←");
        returnButton.setSize(50, 50);
        returnButton.addActionListener(e -> {
            log.info("return button clicked");
            frame.showTitleScreenView();
        });

        JLabel headerLabel = new JLabel("Cat and Mouse", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        headPanel.add(headerLabel, BorderLayout.CENTER);
        headPanel.add(returnButton, BorderLayout.WEST);
        return headPanel;
    }

    public void updateLobby(AvailableGamesMessage message) {
        int index = lobbyList.getSelectedIndex();
        var selected = index != -1 ? lobbyListModel.get(index).name() : null;

        lobbyListModel.removeAllElements();
        lobbyListModel.addAll(message.games());

        if (selected != null) {
            for (int i = 0; i < lobbyListModel.size(); ++i) {
                if (selected.equals(lobbyListModel.get(i).name())) {
                    lobbyList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}
