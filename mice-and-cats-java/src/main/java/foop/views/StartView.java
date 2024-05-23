package foop.views;

import foop.actions.Exit;
import foop.entities.Player;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class StartView extends JPanel implements View {


    private final GameFrame frame;

    public StartView(GameFrame frame) {
        this.frame = frame;
        render();
    }


    public void render() {

        setLayout(new BorderLayout());

        JPanel headPanel = getHeadPanel();
        add(headPanel, BorderLayout.NORTH);

        JTextField tf = new JTextField();
        tf.setEditable(true);
        tf.setPreferredSize(new Dimension(200, 30));


        JButton startButton = new JButton("Start");
        startButton.setBackground(Color.GREEN);
        startButton.setForeground(Color.BLUE);
        startButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
        startButton.addActionListener(actionEvent -> {
            log.info("New Player with name {}", tf.getText());
            new Player(tf.getText());
            //Todo
            frame.getCardLayout().show(frame.getMainPanel(), "BoardView");
        });


        JButton exitButton = new JButton("Exit");
        exitButton.setForeground(Color.BLUE);
        exitButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
        exitButton.setAction(new Exit(frame, frame.getClient()));


        JPanel northPanel = new JPanel();
        northPanel.add(tf);
        add(northPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.add(startButton);
        southPanel.add(exitButton);
        add(southPanel, BorderLayout.SOUTH);

    }

    private void view() {

    }

    private JPanel getHeadPanel() {
        JPanel headPanel =  new JPanel();
        headPanel.setLayout(new BorderLayout());

        JButton returnButton = new JButton("←");
        returnButton.setSize(50, 50);
        returnButton.addActionListener(
                actionEvent -> {
                    log.info("return button clicked");
                    frame.getCardLayout().show(frame.getMainPanel(), "EntreView");
                }
        );

        JLabel headerLabel = new JLabel("Cat and Mouse", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        headPanel.add(headerLabel, BorderLayout.CENTER);
        headPanel.add(returnButton, BorderLayout.WEST);
        return headPanel;
    }
}
