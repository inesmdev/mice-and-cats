package foop.views;

import foop.Assets;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class GameOverVictoryView extends JPanel {

    public GameOverVictoryView(GameFrame frame) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel headerLabel = new JLabel("Victory!", SwingConstants.CENTER);
        headerLabel.setBackground(new Color(255, 255, 255, 200));
        headerLabel.setOpaque(true);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 80));

        add(headerLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setOpaque(false);

        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));
        panel.add(Box.createRigidArea(new Dimension()));

        JButton mainMenu = new JButton("Victory is ours, squeak and rejoice!");
        mainMenu.setAlignmentX(CENTER_ALIGNMENT);
        mainMenu.addActionListener(e -> {
            log.info("Return to main menu");
            frame.showTitleScreenView();
        });

        panel.add(mainMenu);
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
        var image = Assets.getInstance().getVictory();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        super.paint(g);
    }
}
