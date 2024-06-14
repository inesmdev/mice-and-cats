package foop.views;

import foop.Client;
import foop.message.AvailableGamesMessage;
import foop.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

@Getter
@Slf4j
public class GameFrame extends JFrame {

    private final Client client;
    @Setter
    @Getter
    private String playerName;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    @Setter
    private String gameName;

    private final JoinGameView joinGameView;
    private final CreateGameView createGameView;

    public GameFrame(Client client) {
        this.client = client;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Cat and Mouse");
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            //            @Override
            public void windowClosed(WindowEvent e) {
                client.close();
                try {
                    client.getSocket().close();
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });

        joinGameView = new JoinGameView(this);
        createGameView = new CreateGameView(this);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add("TitleScreenView", new TitleScreenView(this));
        mainPanel.add("JoinGameView", joinGameView);
        mainPanel.add("BoardView", new BoardView(this));
        mainPanel.add("CreateGameView", createGameView);

        add(mainPanel);
        setLocationByPlatform(true);

        showTitleScreenView();
    }

    public void showTitleScreenView() {
        this.cardLayout.show(mainPanel, "TitleScreenView");
    }

    public void showBoardView() {
        cardLayout.show(mainPanel, "BoardView");
    }

    public void showJoinGameView() {
        cardLayout.show(mainPanel, "JoinGameView");
    }

    public void showCreateGameView() {
        createGameView.setDefaultName(playerName);
        cardLayout.show(mainPanel, "CreateGameView");
    }

    public void send(Message message) {
        client.send(message);
    }

    public void exit() {
        dispose();
        client.close();
        try {
            client.getSocket().close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public void updateLobby(AvailableGamesMessage m) {
        joinGameView.updateLobby(m);
    }
}
