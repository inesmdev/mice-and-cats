package foop.views;

import foop.Client;
import foop.message.AvailableGamesMessage;
import foop.message.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
public class GameFrame extends JFrame {

    private static final String TITLE_SCREEN_VIEW = "TitleScreenView";
    private static final String JOIN_GAME_VIEW = "JoinGameView";
    private static final String BOARD_VIEW = "BoardView";
    private static final String CREATE_GAME_VIEW = "CreateGameView";

    @Getter
    private final Client client;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final JoinGameView joinGameView;
    private final CreateGameView createGameView;

    public GameFrame(Client client) {
        this.client = client;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Cat and Mouse");
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                client.close();
                log.info("aaaa");
            }
        });

        joinGameView = new JoinGameView(this);
        createGameView = new CreateGameView(this);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(TITLE_SCREEN_VIEW, new TitleScreenView(this));
        mainPanel.add(JOIN_GAME_VIEW, joinGameView);
        mainPanel.add(BOARD_VIEW, new BoardView(this));
        mainPanel.add(CREATE_GAME_VIEW, createGameView);

        add(mainPanel);
        setLocationByPlatform(true);

        showTitleScreenView();

        setVisible(true);
    }

    public void showTitleScreenView() {
        this.cardLayout.show(mainPanel, TITLE_SCREEN_VIEW);
    }

    public void showBoardView() {
        cardLayout.show(mainPanel, BOARD_VIEW);
    }

    public void showJoinGameView() {
        cardLayout.show(mainPanel, JOIN_GAME_VIEW);
    }

    public void showCreateGameView() {
        createGameView.setDefaultName(client.getPlayerName());
        cardLayout.show(mainPanel, CREATE_GAME_VIEW);
    }

    public void updateLobby(AvailableGamesMessage m) {
        joinGameView.updateLobby(m);
    }
}
