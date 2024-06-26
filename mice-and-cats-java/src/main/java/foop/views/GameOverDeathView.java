package foop.views;

import foop.Assets;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class GameOverDeathView extends JPanel {

    private final JLabel headerLabel;
    private final JButton exitBtn;
    private Image image;

    public GameOverDeathView(GameFrame frame) {
        setLayout(new BorderLayout());
        setOpaque(false);

        headerLabel = new JLabel("", SwingConstants.CENTER);
        headerLabel.setBackground(new Color(255, 0, 0, 150));
        headerLabel.setOpaque(true);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 80));

        add(headerLabel, BorderLayout.NORTH);


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setOpaque(false);

        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));

        exitBtn = new JButton();
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            log.info("Return to main menu");
            frame.showTitleScreenView();
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

    public enum Kind {
        YOU_DIED,
        ALL_TEAMMATES_DIED,
        OUT_OF_TIME,
    }

    void setAllButYouDied(Kind kind) {
        switch (kind) {
            case YOU_DIED -> {
                headerLabel.setText("YOU DIED!");
                exitBtn.setText("Rest in Peace.");
                image = Assets.getInstance().getDeath();
            }
            case ALL_TEAMMATES_DIED -> {
                headerLabel.setText("All teammates died!");
                exitBtn.setText("Retreat in sorrow.");
                image = Assets.getInstance().getDeath();
            }
            case OUT_OF_TIME -> {
                headerLabel.setText("Out of time!");
                exitBtn.setText("Time waits for no one. Not even you!");
                image = Assets.getInstance().getOutOfTime();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        super.paint(g);
    }
}
