package foop.views;

import foop.Client;
import foop.actions.Exit;
import foop.message.CreateGameMessage;
import foop.message.InitialMessage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.io.IOException;

import static foop.message.Message.serialize;

@Slf4j
public class EntreView extends JPanel implements View {

    private final GameFrame frame;

    public EntreView(GameFrame frame) {
        this.frame = frame;
        render();
    }

    public void render() {

        setLayout(new BorderLayout());

        // Create a header
        JLabel headerLabel = new JLabel("Cat and Mouse", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(headerLabel, BorderLayout.NORTH);


        // Create Game Button:
        JButton createBtn = new JButton("Create Game");
        JButton joinBtn = new JButton("Join Game");

        JTextField tf = new JTextField();
        tf.setEditable(true);
        tf.setPreferredSize(new Dimension(200, 30));
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
                log.info("changedUpdate: " + e.toString());
            }
        });

        createBtn.addActionListener(actionEvent -> {
            var playerName = tf.getText();
            frame.setPlayName(playerName);
            log.info("New Player with name {}", playerName);
            frame.send(new InitialMessage(playerName));

            log.info("Create Game");
            frame.send(new CreateGameMessage("game1"));

            frame.showBoardView();
        });
        createBtn.setEnabled(false);


        joinBtn.addActionListener(e -> {
            log.info("Join Game");
            var playerName = tf.getText();
            log.info("New Player with name {}", playerName);
            frame.showStartView();
        });
        joinBtn.setEnabled(false);

        // Create name field:

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(tf);
        buttonPanel.add(createBtn);
        buttonPanel.add(joinBtn);
        add(buttonPanel, BorderLayout.CENTER);

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
