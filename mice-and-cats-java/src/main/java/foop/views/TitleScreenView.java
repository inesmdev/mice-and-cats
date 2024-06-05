package foop.views;

import foop.message.InitialMessage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

@Slf4j
public class TitleScreenView extends JPanel {

    private final GameFrame frame;

    public TitleScreenView(GameFrame frame) {
        this.frame = frame;
        render();
    }

    private void render() {

        setLayout(new BorderLayout());

        // Create a header
        JLabel headerLabel = new JLabel("Cat and Mouse", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(headerLabel, BorderLayout.NORTH);

        // Create name field:
        JPanel panel = new JPanel();
        //panel.setBorder(BorderFactory.createLineBorder(Color.RED));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        JLabel nameLabel = new JLabel("Enter a name:", SwingConstants.CENTER);

        JTextField tf = new JTextField();
        tf.setEditable(true);
        tf.setPreferredSize(new Dimension(100, 20));

        JPanel namePanel = new JPanel();
        namePanel.setMaximumSize(new Dimension(200, 40));
        //namePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        namePanel.add(nameLabel);
        namePanel.add(tf);
        panel.add(namePanel);



        // Create buttons panel:
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(300, 75));
        //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JButton createBtn = new JButton("Create Game");
        JButton joinBtn = new JButton("Join Game");
        buttonPanel.add(createBtn);
        buttonPanel.add(joinBtn);

        createBtn.addActionListener(e -> {
            var playerName = tf.getText();
            log.info("New Player with name {}", playerName);
            if (frame.getPlayerName() == null || frame.getPlayerName().isEmpty()) {
                frame.setPlayerName(playerName);
                frame.send(new InitialMessage(playerName));
            }
            frame.showCreateGameView();
        });
        createBtn.setEnabled(false);

        joinBtn.addActionListener(e -> {
            log.info("Join Game");
            var playerName = tf.getText();
            log.info("New Player with name {}", playerName);
            if (frame.getPlayerName() == null || frame.getPlayerName().isEmpty()) {
                frame.setPlayerName(playerName);
                frame.send(new InitialMessage(playerName));
            }
            frame.showJoinGameView();
        });
        joinBtn.setEnabled(false);
        panel.add(buttonPanel, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);

        // Document listener for buttons:
        tf.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                createBtn.setEnabled(!tf.getText().isEmpty());
                joinBtn.setEnabled(!tf.getText().isEmpty());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                createBtn.setEnabled(!tf.getText().isEmpty());
                joinBtn.setEnabled(!tf.getText().isEmpty());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                log.info("changedUpdate: {}", e.toString());
            }
        });

        // Status panel:
        JPanel statusPanel = new JPanel();
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> {
            log.info("Exit");
            exitBtn.setEnabled(false);
            frame.exit();
        });

        statusPanel.add(exitBtn);
        add(statusPanel, BorderLayout.SOUTH);
    }
}
