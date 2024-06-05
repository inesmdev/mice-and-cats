package foop.views;

import foop.message.AvailableGamesMessage;
import foop.message.JoinGameMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class JoinGameView extends JPanel {


    private final GameFrame frame;
    @Getter
    private JList<AvailableGamesMessage.Game> lobbyList;
    private boolean firstTime = true;

    public JoinGameView(GameFrame frame) {
        this.frame = frame;
        render();
    }


    private void render() {

        setLayout(new BorderLayout());

        JPanel headPanel = getHeadPanel();
        add(headPanel, BorderLayout.NORTH);

        JButton startButton = new JButton("Start");

        lobbyList = new JList<>(frame.getLobbyList());
        lobbyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lobbyList.addListSelectionListener(e -> startButton.setEnabled(true));
        lobbyList.setBounds(100, 100, 200, 30);
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


        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> frame.exit());


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.add(startButton);
        southPanel.add(exitButton);
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
}
