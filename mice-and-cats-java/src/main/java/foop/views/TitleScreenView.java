package foop.views;

import foop.Assets;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Random;

@Slf4j
public class TitleScreenView extends JPanel {

    private final GameFrame frame;

    public TitleScreenView(GameFrame frame) {
        this.frame = frame;
        render();
    }

    private void render() {

        setLayout(new BorderLayout());
        setOpaque(false);

        // Create a header
        JLabel headerLabel = new JLabel("Cat and Mouse", SwingConstants.CENTER);
        headerLabel.setBackground(new Color(255, 255, 255, 200));
        headerLabel.setOpaque(true);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 80));

        add(headerLabel, BorderLayout.NORTH);

        // Create name field:
        JPanel panel = new JPanel();

        //panel.setBorder(BorderFactory.createLineBorder(Color.RED));
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setOpaque(false);

        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));

        JLabel nameLabel = new JLabel("Enter your name:", SwingConstants.CENTER);

        JTextField tf = new JTextField(randomUsername());
        tf.setEditable(true);
        tf.setPreferredSize(new Dimension(100, 20));

        JPanel namePanel = new JPanel();
        namePanel.setBackground(new Color(255, 255, 255, 200));
        namePanel.setMaximumSize(new Dimension(1000, 40));
        //namePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        namePanel.add(nameLabel);
        namePanel.add(tf);
        panel.add(namePanel);


        //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        JButton createBtn = new JButton("Create Game");
        createBtn.setAlignmentX(CENTER_ALIGNMENT);

        JButton joinBtn = new JButton("Join Game");
        joinBtn.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(createBtn);
        panel.add(joinBtn);

        Runnable updateButtons = () -> {
            boolean enabled = !tf.getText().isBlank();
            createBtn.setEnabled(enabled);
            joinBtn.setEnabled(enabled);
        };
        updateButtons.run();

        createBtn.addActionListener(e -> {
            var playerName = tf.getText();
            log.info("New Player with name {}", playerName);
            frame.getClient().setPlayerName(playerName);
            frame.showCreateGameView();
        });

        joinBtn.addActionListener(e -> {
            log.info("Join Game");
            var playerName = tf.getText();
            log.info("New Player with name {}", playerName);
            frame.getClient().setPlayerName(playerName);
            frame.showJoinGameView();
        });

        // Document listener for buttons:
        tf.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtons.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtons.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                log.info("changedUpdate: {}", e.toString());
            }
        });

        JButton exitBtn = new JButton("Exit");
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            log.info("Exit");
            exitBtn.setEnabled(false);
            frame.dispose();
        });

        panel.add(exitBtn);
        panel.add(Box.createRigidArea(new Dimension()));

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setOpaque(false);
        panel.setAlignmentY(BOTTOM_ALIGNMENT);
        panel.setPreferredSize(new Dimension(1, 1));

        p2.add(Box.createGlue());
        p2.add(panel, BorderLayout.SOUTH);
        p2.add(Box.createGlue());

        add(p2, BorderLayout.CENTER);
    }

    @Override
    public void paint(Graphics g) {
        var image = Assets.getInstance().getTitleScreenBackground();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        super.paint(g);
    }

    private String randomUsername() {
        String[] adjectives = {
                "Brave", "Clever", "Happy", "Bright", "Quick", "Calm", "Eager",
                "Gentle", "Jolly", "Kind", "Lively", "Proud", "Witty", "Bold", "Daring"
        };

        String[] animals = {
                "Lion", "Tiger", "Bear", "Wolf", "Eagle", "Shark", "Dolphin",
                "Whale", "Penguin", "Koala", "Panda", "Elephant", "Giraffe", "Zebra", "Leopard"
        };

        Random random = new Random();
        String adjective = adjectives[random.nextInt(adjectives.length)];
        String animal = animals[random.nextInt(animals.length)];

        return adjective + animal;
    }
}
