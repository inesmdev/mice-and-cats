package foop.views;

import foop.actions.Exit;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

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

        // Create two Buttons:
        // Create Game Button:
        JButton createBtn = new JButton("Create Game");
        createBtn.addActionListener(actionEvent -> {
            log.info("Create Game");
        });

        JButton joinBtn = new JButton("Join Game");
        joinBtn.addActionListener(e -> {
            log.info("Join Game");
            frame.getCardLayout().show(frame.getMainPanel(), "StartView");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createBtn);
        buttonPanel.add(joinBtn);
        add(buttonPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        JButton exitBtn = new JButton("Exit");
        exitBtn.setAction(new Exit(frame, frame.getClient()));

        statusPanel.add(exitBtn);
        add(statusPanel, BorderLayout.SOUTH);

    }
}
