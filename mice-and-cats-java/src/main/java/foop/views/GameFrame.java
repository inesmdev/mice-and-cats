package foop.views;

import foop.Client;
import foop.message.AvailableGamesMessage;
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
    private static final String GAME_OVER_VICTORY_VIEW = "GameOverVictoryView";
    private static final String GAME_OVER_DEATH_VIEW = "GameOverDeathView";

    @Getter
    private final Client client;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final JoinGameView joinGameView;
    private final CreateGameView createGameView;
    private final GameOverDeathView gameOverDeathView;
    private final BoardView boardView;

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
        gameOverDeathView = new GameOverDeathView(this);
        boardView = new BoardView(this);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(TITLE_SCREEN_VIEW, new TitleScreenView(this));
        mainPanel.add(JOIN_GAME_VIEW, joinGameView);
        mainPanel.add(BOARD_VIEW, boardView);
        mainPanel.add(CREATE_GAME_VIEW, createGameView);
        mainPanel.add(GAME_OVER_VICTORY_VIEW, new GameOverVictoryView(this));
        mainPanel.add(GAME_OVER_DEATH_VIEW, gameOverDeathView);

        add(mainPanel);
        setLocationByPlatform(true);

        showTitleScreenView();

        setVisible(true);
    }

    public void showTitleScreenView() {
        this.cardLayout.show(mainPanel, TITLE_SCREEN_VIEW);
    }

    public void showBoardView() {
        boardView.startNewGame();
        cardLayout.show(mainPanel, BOARD_VIEW);
    }

    public void showJoinGameView() {
        cardLayout.show(mainPanel, JOIN_GAME_VIEW);
    }

    public void showCreateGameView() {
        createGameView.setDefaultName(client.getPlayerName());
        cardLayout.show(mainPanel, CREATE_GAME_VIEW);
    }

    public void showGameOverVictoryView() {
        cardLayout.show(mainPanel, GAME_OVER_VICTORY_VIEW);
    }

    public void showGameOverDeathView(boolean allButYouDied) {
        gameOverDeathView.setAllButYouDied(allButYouDied);
        cardLayout.show(mainPanel, GAME_OVER_DEATH_VIEW);
    }

    public void updateLobby(AvailableGamesMessage m) {
        joinGameView.updateLobby(m);
        boardView.updateLobby(m);
    }
}
