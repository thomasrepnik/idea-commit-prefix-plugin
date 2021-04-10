package ch.repnik.intellij.settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PluginSettingsForm {
    private JPanel mainPanel;
    private JTextField txtWrapLeft;
    private JTextField txtWrapRight;

    public PluginSettingsForm(){
        txtWrapLeft.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (txtWrapLeft.getText().length() >= 5 ) // limit textfield to 5 characters
                    e.consume();
            }
        });

        txtWrapRight.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (txtWrapRight.getText().length() >= 5 ) // limit textfield to 5 characters
                    e.consume();
            }
        });
    }

    public JPanel getPanel(){
        return mainPanel;
    }

    public String getWrapRight(){
        return txtWrapRight.getText();
    }

    public String getWrapLeft() {return txtWrapLeft.getText();}

    public void resetEditorFrom(PluginSettings settings){
        this.txtWrapLeft.setText(settings.getWrapLeft());
        this.txtWrapRight.setText(settings.getWrapRight());
    }
}
