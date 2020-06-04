package ch.repnik.intellij.settings;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PluginSettingsForm {
    private JPanel mainPanel;
    private JTextField txtDelimiter;

    public PluginSettingsForm(){
        txtDelimiter.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (txtDelimiter.getText().length() >= 5 ) // limit textfield to 5 characters
                    e.consume();
            }
        });
    }

    public JPanel getPanel(){
        return mainPanel;
    }

    public String getDelimiter(){
        return txtDelimiter.getText();
    }

    public void resetEditorFrom(PluginSettings settings){
        this.txtDelimiter.setText(settings.getCommitMessageDelimiter());
    }
}
