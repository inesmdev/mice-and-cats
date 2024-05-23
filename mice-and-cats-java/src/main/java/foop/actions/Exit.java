package foop.actions;

import foop.Client;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

@Slf4j
public class Exit extends AbstractAction {

    private final JFrame frame;
    private final Client client;

    public Exit(JFrame frame, Client client) {
        super("Exit");
        this.frame = frame;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
        client.setRunning(false);
        try {
            client.getSocket().close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
