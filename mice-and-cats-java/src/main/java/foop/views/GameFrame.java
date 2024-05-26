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
import java.io.IOException;

@Getter
@Slf4j
public class GameFrame extends JFrame {

    private final Client client;
    private CardLayout cardLayout;
    private JPanel mainPanel;


    public GameFrame(Client client) {
        this.client = client;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Cat and Mouse");
        setSize(800, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            //            @Override
            public void windowClosed(WindowEvent e) {
                client.setRunning(false);
                try {
                    client.getSocket().close();
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add("EntreView", new EntreView(this));
        mainPanel.add("StartView", new StartView(this));
        mainPanel.add("BoardView", new BoardView(this));

        add(mainPanel);
        setLocationByPlatform(true);

        showEntreView();
    }

    public void showStartView() {
        this.cardLayout.show(mainPanel, "StartView");
    }

    public void showBoardView() {
        cardLayout.show(mainPanel, "BoardView");
    }

    public void showEntreView() {
        cardLayout.show(mainPanel, "EntreView");
    }

    public void send(Message message) {
        client.send(message);
    }

    public void exit() {
        dispose();
        client.setRunning(false);
        try {
            client.getSocket().close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public void setPlayName(String playerName) {
        client.setPlayerName(playerName);
    }

    public String getPlayerName() {
        return client.getPlayerName();
    }

    public DefaultListModel<AvailableGamesMessage.Game> getLobbyList() {
        return client.getLobbyListModel();
    }
}
