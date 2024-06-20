package foop.views;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class CreateGameView extends JPanel {

    private final GameFrame frame;
    private final JTextField gameName;

    public CreateGameView(GameFrame frame) {
        this.frame = frame;
        this.gameName = new JTextField();
        render();
    }

    private void render() {
        this.setLayout(new BorderLayout());

        add(getHeadPanel(), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        //panel.setBorder(BorderFactory.createLineBorder(Color.RED));


        JLabel gameNameLabel = new JLabel("Game Name");

        gameName.setPreferredSize(new Dimension(150, 20));

        JPanel gameNamePanel = new JPanel();
        //gameNamePanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        gameNamePanel.add(gameNameLabel);
        gameNamePanel.setMaximumSize(new Dimension(300, 50));
        gameNamePanel.add(gameName);

        panel.add(gameNamePanel, BorderLayout.CENTER);


        JLabel gameSizeLabel = new JLabel("Game size");

        JTextField gameSize = new JTextField();
        gameSize.setPreferredSize(new Dimension(150, 20));

        JPanel gameSizePanel = new JPanel();
        gameSizePanel.setMaximumSize(new Dimension(300, 50));
        //gameSizePanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        gameSizePanel.add(gameSizeLabel);
        gameSizePanel.add(gameSize);

        panel.add(gameSizePanel, BorderLayout.CENTER);

        this.add(panel, BorderLayout.CENTER);

        JButton createGameButton = new JButton("Create Game");
        createGameButton.addActionListener(e -> {
            log.info("Create Game");
            var name = gameName.getText();
            int size;
            try {
                size = Integer.parseInt(gameSize.getText());
            } catch (NumberFormatException ex) {
                size = 0;
            }
            var returnSize = size < 1 ? 2 : size;
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid name");
                return;
            }
            frame.getClient().createGame(name, returnSize);
        });
        this.add(createGameButton, BorderLayout.SOUTH);

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

        JLabel headerLabel = new JLabel("Create a game", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        headPanel.add(headerLabel, BorderLayout.CENTER);
        headPanel.add(returnButton, BorderLayout.WEST);
        return headPanel;
    }

    public void setDefaultName(String playerName) {
        gameName.setText(playerName + "'s game");
    }
}

