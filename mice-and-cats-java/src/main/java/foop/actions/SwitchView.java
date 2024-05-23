package foop.actions;

import foop.views.View;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;

@Slf4j
public class SwitchView extends AbstractAction {

    private final JFrame frame;
    private View newView;

    public SwitchView(String name, JFrame frame, View view) {
        super(name);
        this.frame = frame;
        this.newView = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.debug("Switch view action called");
        frame.setVisible(false);

        frame.dispose();
    }
}
