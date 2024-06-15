package foop.views;

import foop.Assets;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class GameOverDeathView extends JPanel {

    private final JLabel headerLabel;

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

        JButton exitBtn = new JButton("Retreat in sorrow.");
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

    void setAllButYouDied(boolean allButYouDied) {
        if (allButYouDied) {
            headerLabel.setText("All teammates died!");
        } else {
            headerLabel.setText("YOU DIED!");
        }
    }

    @Override
    public void paint(Graphics g) {
        var image = Assets.getInstance().getDeath();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        super.paint(g);
    }
}
